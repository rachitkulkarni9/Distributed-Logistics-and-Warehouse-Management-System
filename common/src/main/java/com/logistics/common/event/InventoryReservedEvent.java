package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Published by Inventory Service to {@code inventory-reserved} when all
 * requested SKUs are successfully reserved. Triggers shipment creation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InventoryReservedEvent extends BaseEvent {

    private UUID orderId;
    private String warehouseId;
    private List<ReservedItem> reservedItems;

    @Builder
    public InventoryReservedEvent(String correlationId, UUID orderId,
                                   String warehouseId, List<ReservedItem> reservedItems) {
        super(EventType.INVENTORY_RESERVED, correlationId);
        this.orderId = orderId;
        this.warehouseId = warehouseId;
        this.reservedItems = reservedItems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ReservedItem {
        private String sku;
        private int quantityReserved;
        private String locationCode;
    }
}
