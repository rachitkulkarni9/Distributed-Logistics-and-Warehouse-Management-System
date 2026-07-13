package com.logistics.gateway.filter;

import com.logistics.gateway.config.JwtConfig;
import com.logistics.gateway.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway filter that validates the JWT {@code Authorization: Bearer <token>} header
 * on every protected route. On success, it forwards the extracted claims as
 * downstream request headers so services can trust them without re-validating.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;

    /** Paths that bypass JWT validation (public endpoints). */
    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/refresh",
        "/actuator/health",
        "/actuator/info",
        "/swagger-ui",
        "/v3/api-docs",
        "/webjars"
    );

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or malformed Authorization header for path: {}", path);
                return unauthorized(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            try {
                if (!jwtUtil.isTokenValid(token)) {
                    return unauthorized(exchange, "Token is expired or invalid");
                }

                String subject = jwtUtil.extractSubject(token);
                String role    = jwtUtil.extractRole(token);

                // Forward claims to downstream services as trusted headers
                ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", subject)
                    .header("X-User-Role", role)
                    .header("X-Correlation-ID", extractOrGenerateCorrelationId(request))
                    .build();

                log.debug("JWT validated for user={} role={} path={}", subject, role, path);
                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("JWT validation error for path {}: {}", path, e.getMessage());
                return unauthorized(exchange, "Token validation failed");
            }
        };
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private String extractOrGenerateCorrelationId(ServerHttpRequest request) {
        String existing = request.getHeaders().getFirst("X-Correlation-ID");
        return existing != null ? existing : java.util.UUID.randomUUID().toString();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"success\":false,\"message\":\"%s\"}", message);
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Data
    public static class Config {
        // Placeholder for per-route filter configuration
    }
}
