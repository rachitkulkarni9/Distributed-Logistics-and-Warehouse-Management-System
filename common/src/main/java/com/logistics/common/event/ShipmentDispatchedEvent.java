package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by Shipment Service when a carrier picks up the package.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShipmentDispatchedEvent extends BaseEvent {

    private UUID shipmentId;
    private UUID orderId;
    private String trackingNumber;
    private String carrier;
    private Instant dispatchedAt;
    private String currentLocation;

    @Builder
    public ShipmentDispatchedEvent(String correlationId, UUID shipmentId, UUID orderId,
                                    String trackingNumber, String carrier,
                                    Instant dispatchedAt, String currentLocation) {
        super(EventType.SHIPMENT_DISPATCHED, correlationId);
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.dispatchedAt = dispatchedAt;
        this.currentLocation = currentLocation;
    }
}
