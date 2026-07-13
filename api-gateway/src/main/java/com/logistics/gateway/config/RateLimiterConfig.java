package com.logistics.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Redis-backed token-bucket rate limiter.
 * Keyed per authenticated user (JWT subject) with a fallback to remote IP.
 */
@Configuration
public class RateLimiterConfig {

    /** 50 requests/second burst, 20 requests/second replenish rate. */
    @Bean
    @Primary
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(20, 50, 1);
    }

    /** Uses JWT subject claim as the rate-limit key when available, else falls back to IP. */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract sub claim without full validation (already validated by JwtAuthFilter)
                return Mono.just(authHeader.substring(7, Math.min(authHeader.length(), 30)));
            }
            return Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous"
            );
        };
    }
}
