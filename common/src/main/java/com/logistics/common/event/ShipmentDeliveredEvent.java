package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Published by Shipment Service when a package is confirmed delivered.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShipmentDeliveredEvent extends BaseEvent {

    private UUID shipmentId;
    private UUID orderId;
    private String trackingNumber;
    private Instant deliveredAt;
    private String signedBy;

    @Builder
    public ShipmentDeliveredEvent(String correlationId, UUID shipmentId, UUID orderId,
                                   String trackingNumber, Instant deliveredAt, String signedBy) {
        super(EventType.SHIPMENT_DELIVERED, correlationId);
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.deliveredAt = deliveredAt;
        this.signedBy = signedBy;
    }
}
