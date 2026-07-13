package com.logistics.order.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.enums.OrderStatus;
import com.logistics.common.event.InventoryFailedEvent;
import com.logistics.common.event.InventoryReservedEvent;
import com.logistics.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumes inventory events to drive saga compensation and status updates.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "order-service-group")
    @Transactional
    public void onInventoryReserved(@Payload InventoryReservedEvent event,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    Acknowledgment ack) {
        log.info("Received InventoryReservedEvent: orderId={} correlationId={}",
            event.getOrderId(), event.getCorrelationId());

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.INVENTORY_RESERVED);
            orderRepository.save(order);
            log.info("Order {} status updated to INVENTORY_RESERVED", order.getId());
        }, () -> log.warn("Order not found for id={}", event.getOrderId()));

        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_FAILED, groupId = "order-service-group")
    @Transactional
    public void onInventoryFailed(@Payload InventoryFailedEvent event,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   Acknowledgment ack) {
        log.warn("Received InventoryFailedEvent: orderId={} reason={}",
            event.getOrderId(), event.getReason());

        // Saga compensation: cancel the order
        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Order {} cancelled due to inventory failure: {}", order.getId(), event.getReason());
            // TODO: publish notification event for cancellation
        }, () -> log.warn("Order not found for id={}", event.getOrderId()));

        ack.acknowledge();
    }
}
