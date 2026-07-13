package com.logistics.auth.service;

import com.logistics.auth.entity.RefreshToken;
import com.logistics.auth.entity.User;
import com.logistics.auth.repository.RefreshTokenRepository;
import com.logistics.auth.repository.UserRepository;
import com.logistics.auth.security.JwtService;
import com.logistics.common.dto.auth.AuthRequest;
import com.logistics.common.dto.auth.AuthResponse;
import com.logistics.common.dto.auth.RegisterRequest;
import com.logistics.common.exception.BusinessRuleException;
import com.logistics.common.exception.DuplicateResourceException;
import com.logistics.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Handles registration, login, token refresh, and logout.
 * Refresh tokens are persisted to allow revocation; access tokens are
 * short-lived and blacklisted in Redis on logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:";
    private static final long ACCESS_TOKEN_TTL_SECONDS = 3600;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .role(request.getRole())
            .build();

        user = userRepository.save(user);
        log.info("User registered: id={} email={} role={}", user.getId(), user.getEmail(), user.getRole());

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findActiveByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Revoke existing refresh tokens before issuing new ones
        refreshTokenRepository.revokeAllUserTokens(user);

        log.info("User logged in: id={} email={}", user.getId(), user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new BusinessRuleException("Refresh token not found", "INVALID_REFRESH_TOKEN"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessRuleException("Refresh token is expired or revoked", "INVALID_REFRESH_TOKEN");
        }

        // Rotate: revoke old token, issue new pair
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        log.info("Token refreshed for user: id={}", user.getId());
        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(String accessToken, String userId) {
        // Blacklist the access token in Redis until it would naturally expire
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(blacklistKey, userId, ACCESS_TOKEN_TTL_SECONDS, TimeUnit.SECONDS);

        // Revoke all refresh tokens for the user
        userRepository.findById(java.util.UUID.fromString(userId)).ifPresent(refreshTokenRepository::revokeAllUserTokens);

        log.info("User logged out: userId={}", userId);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token));
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshValue = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
            .token(refreshValue)
            .user(user)
            .expiresAt(Instant.now().plusMillis(604_800_000))
            .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshValue)
            .tokenType("Bearer")
            .expiresIn(ACCESS_TOKEN_TTL_SECONDS)
            .user(AuthResponse.UserSummary.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build())
            .build();
    }
}
