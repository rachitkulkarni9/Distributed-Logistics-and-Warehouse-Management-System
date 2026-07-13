package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Published by Order Service to the {@code order-created} topic when a new
 * order is successfully persisted. Triggers inventory reservation in the saga.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderCreatedEvent extends BaseEvent {

    private UUID orderId;
    private String customerId;
    private String customerEmail;
    private List<OrderLineItem> lineItems;
    private BigDecimal totalAmount;
    private String currency;
    private ShippingAddress shippingAddress;

    @Builder
    public OrderCreatedEvent(String correlationId, UUID orderId, String customerId,
                              String customerEmail, List<OrderLineItem> lineItems,
                              BigDecimal totalAmount, String currency,
                              ShippingAddress shippingAddress) {
        super(EventType.ORDER_CREATED, correlationId);
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.lineItems = lineItems;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.shippingAddress = shippingAddress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OrderLineItem {
        private String sku;
        private int quantity;
        private BigDecimal unitPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ShippingAddress {
        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }
}
