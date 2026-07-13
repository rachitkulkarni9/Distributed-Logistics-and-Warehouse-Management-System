package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Published by Shipment Service to {@code shipment-created} after a shipment
 * record is created for a successfully reserved order.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShipmentCreatedEvent extends BaseEvent {

    private UUID shipmentId;
    private UUID orderId;
    private String trackingNumber;
    private String carrier;
    private LocalDate estimatedDeliveryDate;
    private String destinationAddress;

    @Builder
    public ShipmentCreatedEvent(String correlationId, UUID shipmentId, UUID orderId,
                                 String trackingNumber, String carrier,
                                 LocalDate estimatedDeliveryDate, String destinationAddress) {
        super(EventType.SHIPMENT_CREATED, correlationId);
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.destinationAddress = destinationAddress;
    }
}
