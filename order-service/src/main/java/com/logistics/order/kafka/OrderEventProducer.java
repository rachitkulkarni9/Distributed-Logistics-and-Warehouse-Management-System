package com.logistics.order.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Publishes order domain events to Kafka.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event.getOrderId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish OrderCreatedEvent for orderId={}: {}",
                    event.getOrderId(), ex.getMessage());
            } else {
                log.info("Published OrderCreatedEvent: orderId={} partition={} offset={}",
                    event.getOrderId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }
}
