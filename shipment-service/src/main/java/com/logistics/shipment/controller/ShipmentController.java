package com.logistics.shipment.controller;

import com.logistics.common.dto.shipment.ShipmentDto;
import com.logistics.common.response.ApiResponse;
import com.logistics.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Shipment tracking and status management")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment by ID")
    public ResponseEntity<ApiResponse<ShipmentDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.toDto(shipmentService.findById(id))));
    }

    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Track shipment by tracking number")
    public ResponseEntity<ApiResponse<ShipmentDto>> track(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(ApiResponse.success(shipmentService.toDto(shipmentService.findByTracking(trackingNumber))));
    }

    @PutMapping("/{id}/dispatch")
    @Operation(summary = "Mark shipment as dispatched")
    public ResponseEntity<ApiResponse<ShipmentDto>> dispatch(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Shipment dispatched", shipmentService.toDto(shipmentService.dispatch(id))));
    }

    @PutMapping("/{id}/deliver")
    @Operation(summary = "Mark shipment as delivered")
    public ResponseEntity<ApiResponse<ShipmentDto>> deliver(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Shipment delivered", shipmentService.toDto(shipmentService.markDelivered(id))));
    }
}
