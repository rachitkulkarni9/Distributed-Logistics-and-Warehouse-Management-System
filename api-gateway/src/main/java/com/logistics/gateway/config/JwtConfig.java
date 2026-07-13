package com.logistics.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.jwt.*} properties from application.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    /** HMAC-SHA256 signing secret (min 256 bits in production). */
    private String secret;

    /** Token validity in milliseconds. */
    private long expirationMs = 3_600_000; // 1 hour

    /** Refresh token validity in milliseconds. */
    private long refreshExpirationMs = 604_800_000; // 7 days
}
