package com.logistics.gateway.exception;

import com.logistics.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Circuit-breaker fallback endpoints for each downstream service.
 * Returns a structured error response instead of a raw 503 when a service is down.
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<Void>> authFallback() {
        return fallback("Auth Service");
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<Void>> orderFallback() {
        return fallback("Order Service");
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<Void>> inventoryFallback() {
        return fallback("Inventory Service");
    }

    @GetMapping("/shipment")
    public ResponseEntity<ApiResponse<Void>> shipmentFallback() {
        return fallback("Shipment Service");
    }

    @GetMapping("/warehouse")
    public ResponseEntity<ApiResponse<Void>> warehouseFallback() {
        return fallback("Warehouse Service");
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Void>> analyticsFallback() {
        return fallback("Analytics Service");
    }

    private ResponseEntity<ApiResponse<Void>> fallback(String serviceName) {
        log.warn("Circuit breaker triggered for {}", serviceName);
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.error(serviceName + " is temporarily unavailable. Please try again later."));
    }
}
