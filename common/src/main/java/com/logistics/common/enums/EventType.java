package com.logistics.common.enums;

/**
 * Canonical event type identifiers used as Kafka message headers and
 * for routing inside the notification service.
 */
public enum EventType {
    ORDER_CREATED,
    ORDER_CONFIRMED,
    ORDER_CANCELLED,
    INVENTORY_RESERVED,
    INVENTORY_RESERVATION_FAILED,
    INVENTORY_RELEASED,
    SHIPMENT_CREATED,
    SHIPMENT_DISPATCHED,
    SHIPMENT_IN_TRANSIT,
    SHIPMENT_DELIVERED,
    SHIPMENT_FAILED,
    NOTIFICATION_REQUESTED
}
