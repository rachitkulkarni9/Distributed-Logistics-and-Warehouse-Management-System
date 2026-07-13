package com.logistics.order.service;

import com.logistics.common.dto.order.CreateOrderRequest;
import com.logistics.common.dto.order.OrderLineItemRequest;
import com.logistics.common.dto.order.AddressDto;
import com.logistics.common.enums.OrderStatus;
import com.logistics.common.exception.BusinessRuleException;
import com.logistics.order.entity.Order;
import com.logistics.order.kafka.OrderEventProducer;
import com.logistics.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderEventProducer eventProducer;
    @InjectMocks private OrderService orderService;

    @Test
    void createOrder_persistsAndPublishesEvent() {
        CreateOrderRequest req = buildRequest();
        Order saved = Order.builder().id(UUID.randomUUID()).orderNumber("ORD-001")
            .status(OrderStatus.PENDING).totalAmount(java.math.BigDecimal.TEN)
            .customerId("cust-1").customerEmail("a@b.com").correlationId(UUID.randomUUID().toString())
            .currency("USD").build();
        when(orderRepository.save(any())).thenReturn(saved);

        Order result = orderService.createOrder(req);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(eventProducer).publishOrderCreated(any());
    }

    @Test
    void cancelOrder_whenDelivered_throwsBusinessRuleException() {
        UUID id = UUID.randomUUID();
        Order order = Order.builder().id(id).status(OrderStatus.DELIVERED)
            .totalAmount(java.math.BigDecimal.TEN).correlationId("c").currency("USD").build();
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(id))
            .isInstanceOf(BusinessRuleException.class);
    }

    private CreateOrderRequest buildRequest() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setCustomerId("cust-1");
        req.setCustomerEmail("a@b.com");
        OrderLineItemRequest item = new OrderLineItemRequest();
        item.setSku("SKU-001"); item.setQuantity(2);
        req.setLineItems(List.of(item));
        AddressDto addr = AddressDto.builder().street("1 Main St").city("NYC")
            .postalCode("10001").country("US").build();
        req.setShippingAddress(addr);
        return req;
    }
}
