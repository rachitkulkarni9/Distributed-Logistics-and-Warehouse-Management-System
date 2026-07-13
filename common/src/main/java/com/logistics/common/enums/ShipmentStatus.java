package com.logistics.common.enums;

/**
 * Lifecycle states of a shipment.
 */
public enum ShipmentStatus {
    CREATED,
    AWAITING_PICKUP,
    DISPATCHED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED_DELIVERY,
    RETURNED
}
