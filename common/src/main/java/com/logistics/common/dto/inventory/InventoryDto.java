package com.logistics.common.dto.inventory;

import com.logistics.common.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class InventoryDto {

    private UUID id;
    private String sku;
    private String productName;
    private String description;
    private int quantityOnHand;
    private int quantityReserved;
    private int quantityAvailable;
    private int reorderPoint;
    private int reorderQuantity;
    private BigDecimal unitCost;
    private String warehouseId;
    private String locationCode;
    private InventoryStatus status;
    private Instant lastUpdatedAt;
}
