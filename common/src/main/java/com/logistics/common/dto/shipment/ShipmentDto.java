package com.logistics.common.dto.shipment;

import com.logistics.common.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ShipmentDto {

    private UUID id;
    private UUID orderId;
    private String trackingNumber;
    private String carrier;
    private ShipmentStatus status;
    private String originWarehouseId;
    private String destinationAddress;
    private LocalDate estimatedDeliveryDate;
    private Instant dispatchedAt;
    private Instant deliveredAt;
    private double weightKg;
    private String dimensions;
    private Instant createdAt;
    private Instant updatedAt;
}
