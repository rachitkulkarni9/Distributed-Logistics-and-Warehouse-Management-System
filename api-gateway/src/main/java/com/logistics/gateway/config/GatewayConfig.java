package com.logistics.gateway.config;

import com.logistics.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programmatic route definitions.
 * Each downstream service gets its own route with the JWT auth filter applied
 * to protected paths and a circuit breaker for fault tolerance.
 */
@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

            // ── Auth Service ────────────────────────────────────────────────
            .route("auth-service", r -> r
                .path("/api/v1/auth/**", "/api/v1/users/**")
                .filters(f -> f
                    .rewritePath("/api/v1/(?<segment>.*)", "/api/v1/${segment}")
                    .circuitBreaker(c -> c.setName("auth-cb").setFallbackUri("forward:/fallback/auth")))
                .uri("lb://auth-service"))

            // ── Order Service ────────────────────────────────────────────────
            .route("order-service", r -> r
                .path("/api/v1/orders/**")
                .filters(f -> f
                    .filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()))
                    .circuitBreaker(c -> c.setName("order-cb").setFallbackUri("forward:/fallback/order")))
                .uri("lb://order-service"))

            // ── Inventory Service ─────────────────────────────────────────────
            .route("inventory-service", r -> r
                .path("/api/v1/inventory/**")
                .filters(f -> f
                    .filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()))
                    .circuitBreaker(c -> c.setName("inventory-cb").setFallbackUri("forward:/fallback/inventory")))
                .uri("lb://inventory-service"))

            // ── Shipment Service ──────────────────────────────────────────────
            .route("shipment-service", r -> r
                .path("/api/v1/shipments/**")
                .filters(f -> f
                    .filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()))
                    .circuitBreaker(c -> c.setName("shipment-cb").setFallbackUri("forward:/fallback/shipment")))
                .uri("lb://shipment-service"))

            // ── Warehouse Service ─────────────────────────────────────────────
            .route("warehouse-service", r -> r
                .path("/api/v1/warehouses/**")
                .filters(f -> f
                    .filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()))
                    .circuitBreaker(c -> c.setName("warehouse-cb").setFallbackUri("forward:/fallback/warehouse")))
                .uri("lb://warehouse-service"))

            // ── Analytics Service ─────────────────────────────────────────────
            .route("analytics-service", r -> r
                .path("/api/v1/analytics/**")
                .filters(f -> f
                    .filter(jwtAuthFilter.apply(new JwtAuthenticationFilter.Config()))
                    .circuitBreaker(c -> c.setName("analytics-cb").setFallbackUri("forward:/fallback/analytics")))
                .uri("lb://analytics-service"))

            .build();
    }
}
