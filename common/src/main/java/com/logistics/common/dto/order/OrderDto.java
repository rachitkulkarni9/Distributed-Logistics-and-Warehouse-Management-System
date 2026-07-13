package com.logistics.common.dto.order;

import com.logistics.common.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDto {

    private UUID id;
    private String orderNumber;
    private String customerId;
    private String customerEmail;
    private OrderStatus status;
    private List<OrderLineItemDto> lineItems;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private String currency;
    private AddressDto shippingAddress;
    private String trackingNumber;
    private Instant createdAt;
    private Instant updatedAt;
}
