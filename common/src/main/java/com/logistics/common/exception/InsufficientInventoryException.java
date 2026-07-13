package com.logistics.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown by Inventory Service when requested stock cannot be reserved.
 * Maps to HTTP 409.
 */
public class InsufficientInventoryException extends LogisticsException {

    public InsufficientInventoryException(String sku, int requested, int available) {
        super(
            String.format("Insufficient inventory for SKU '%s': requested %d, available %d",
                sku, requested, available),
            HttpStatus.CONFLICT,
            "INSUFFICIENT_INVENTORY"
        );
    }
}
