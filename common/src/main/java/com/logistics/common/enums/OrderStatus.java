package com.logistics.common.enums;

/**
 * Lifecycle states of an order within the saga.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    INVENTORY_RESERVED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
