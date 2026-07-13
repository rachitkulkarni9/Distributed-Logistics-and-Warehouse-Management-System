package com.logistics.common.config;

/**
 * Canonical Kafka topic names shared across all services.
 * Keeping these in one place prevents typos from causing silent routing failures.
 */
public final class KafkaTopics {

    // ─── Business topics ──────────────────────────────────────────────────────
    public static final String ORDER_CREATED         = "order-created";
    public static final String INVENTORY_RESERVED    = "inventory-reserved";
    public static final String INVENTORY_FAILED      = "inventory-failed";
    public static final String SHIPMENT_CREATED      = "shipment-created";
    public static final String SHIPMENT_DISPATCHED   = "shipment-dispatched";
    public static final String SHIPMENT_DELIVERED    = "shipment-delivered";
    public static final String NOTIFICATION_EVENTS   = "notification-events";

    // ─── Dead-letter topics ───────────────────────────────────────────────────
    public static final String ORDER_CREATED_DLT       = "order-created.DLT";
    public static final String INVENTORY_RESERVED_DLT  = "inventory-reserved.DLT";
    public static final String INVENTORY_FAILED_DLT    = "inventory-failed.DLT";
    public static final String SHIPMENT_CREATED_DLT    = "shipment-created.DLT";
    public static final String NOTIFICATION_EVENTS_DLT = "notification-events.DLT";

    private KafkaTopics() {}
}
