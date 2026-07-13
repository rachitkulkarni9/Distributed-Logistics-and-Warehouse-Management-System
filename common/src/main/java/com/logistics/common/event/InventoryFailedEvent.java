package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Published by Inventory Service to {@code inventory-failed} when one or more
 * SKUs cannot be reserved. Triggers order cancellation (saga compensation).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InventoryFailedEvent extends BaseEvent {

    private UUID orderId;
    private String reason;
    private List<FailedItem> failedItems;

    @Builder
    public InventoryFailedEvent(String correlationId, UUID orderId,
                                 String reason, List<FailedItem> failedItems) {
        super(EventType.INVENTORY_RESERVATION_FAILED, correlationId);
        this.orderId = orderId;
        this.reason = reason;
        this.failedItems = failedItems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FailedItem {
        private String sku;
        private int requestedQuantity;
        private int availableQuantity;
    }
}
