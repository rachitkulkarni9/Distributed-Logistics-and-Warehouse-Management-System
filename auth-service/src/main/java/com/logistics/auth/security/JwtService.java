package com.logistics.auth.security;

import com.logistics.auth.config.JwtProperties;
import com.logistics.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Generates and validates JWTs.
 * The gateway re-validates tokens using the same shared secret.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        return buildToken(user, jwtProperties.getExpirationMs(), Map.of(
            "email", user.getEmail(),
            "role",  user.getRole().name(),
            "name",  user.getFullName()
        ));
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, jwtProperties.getRefreshExpirationMs(), Map.of());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token invalid: {}", e.getMessage());
            return false;
        }
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private String buildToken(User user, long ttlMs, Map<String, Object> extraClaims) {
        Date now = new Date();
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(user.getId().toString())
            .claims(extraClaims)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + ttlMs))
            .signWith(signingKey())
            .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
