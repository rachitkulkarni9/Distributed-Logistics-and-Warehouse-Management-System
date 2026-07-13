package com.logistics.order.service;

import com.logistics.common.dto.order.CreateOrderRequest;
import com.logistics.common.dto.order.OrderDto;
import com.logistics.common.enums.OrderStatus;
import com.logistics.common.event.OrderCreatedEvent;
import com.logistics.common.exception.BusinessRuleException;
import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.common.util.OrderNumberGenerator;
import com.logistics.order.entity.Order;
import com.logistics.order.entity.OrderLineItem;
import com.logistics.order.kafka.OrderEventProducer;
import com.logistics.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        String correlationId = UUID.randomUUID().toString();

        Order order = Order.builder()
            .orderNumber(OrderNumberGenerator.generate())
            .customerId(request.getCustomerId())
            .customerEmail(request.getCustomerEmail())
            .status(OrderStatus.PENDING)
            .currency(request.getCurrency())
            .notes(request.getNotes())
            .correlationId(correlationId)
            .shippingStreet(request.getShippingAddress().getStreet())
            .shippingCity(request.getShippingAddress().getCity())
            .shippingState(request.getShippingAddress().getState())
            .shippingPostalCode(request.getShippingAddress().getPostalCode())
            .shippingCountry(request.getShippingAddress().getCountry())
            .totalAmount(BigDecimal.ZERO) // calculated below
            .build();

        // TODO: fetch real product prices from catalog service
        List<OrderLineItem> lineItems = request.getLineItems().stream().map(req -> {
            BigDecimal unitPrice = BigDecimal.valueOf(10.00); // placeholder
            return OrderLineItem.builder()
                .order(order)
                .sku(req.getSku())
                .productName("Product-" + req.getSku())
                .quantity(req.getQuantity())
                .unitPrice(unitPrice)
                .lineTotal(unitPrice.multiply(BigDecimal.valueOf(req.getQuantity())))
                .build();
        }).collect(Collectors.toList());

        order.setLineItems(lineItems);
        order.setTotalAmount(lineItems.stream()
            .map(OrderLineItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        Order saved = orderRepository.save(order);
        log.info("Order created: id={} number={}", saved.getId(), saved.getOrderNumber());

        // Publish saga-starting event
        OrderCreatedEvent event = OrderCreatedEvent.builder()
            .correlationId(correlationId)
            .orderId(saved.getId())
            .customerId(saved.getCustomerId())
            .customerEmail(saved.getCustomerEmail())
            .lineItems(lineItems.stream().map(li ->
                OrderCreatedEvent.OrderLineItem.builder()
                    .sku(li.getSku())
                    .quantity(li.getQuantity())
                    .unitPrice(li.getUnitPrice())
                    .build()).collect(Collectors.toList()))
            .totalAmount(saved.getTotalAmount())
            .currency(saved.getCurrency())
            .shippingAddress(OrderCreatedEvent.ShippingAddress.builder()
                .street(saved.getShippingStreet())
                .city(saved.getShippingCity())
                .state(saved.getShippingState())
                .postalCode(saved.getShippingPostalCode())
                .country(saved.getShippingCountry())
                .build())
            .build();

        eventProducer.publishOrderCreated(event);
        return saved;
    }

    @Transactional(readOnly = true)
    public Order findById(UUID id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<Order> findByCustomer(String customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public Order cancelOrder(UUID id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Cannot cancel an order that has already been shipped or delivered");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public OrderDto toDto(Order order) {
        // TODO: use MapStruct mapper
        return OrderDto.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .customerId(order.getCustomerId())
            .customerEmail(order.getCustomerEmail())
            .status(order.getStatus())
            .totalAmount(order.getTotalAmount())
            .currency(order.getCurrency())
            .trackingNumber(order.getTrackingNumber())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
