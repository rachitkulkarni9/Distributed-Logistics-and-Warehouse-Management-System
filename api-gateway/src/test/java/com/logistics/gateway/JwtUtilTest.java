package com.logistics.gateway;

import com.logistics.gateway.config.JwtConfig;
import com.logistics.gateway.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "test-secret-key-that-is-long-enough-for-hmac-sha256-signing";

    @BeforeEach
    void setUp() {
        JwtConfig config = new JwtConfig();
        config.setSecret(SECRET);
        config.setExpirationMs(3_600_000L);
        jwtUtil = new JwtUtil(config);
    }

    @Test
    void validToken_isAccepted() {
        String token = buildToken("user-123", "ROLE_CUSTOMER", 3_600_000);
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractSubject(token)).isEqualTo("user-123");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ROLE_CUSTOMER");
    }

    @Test
    void expiredToken_isRejected() {
        String token = buildToken("user-123", "ROLE_CUSTOMER", -1000);
        assertThat(jwtUtil.isTokenValid(token)).isFalse();
    }

    @Test
    void malformedToken_isRejected() {
        assertThat(jwtUtil.isTokenValid("not.a.jwt")).isFalse();
    }

    private String buildToken(String subject, String role, long ttlMs) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
            .subject(subject)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ttlMs))
            .signWith(key)
            .compact();
    }
}
