package com.logistics.inventory.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.OrderCreatedEvent;
import com.logistics.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = ".DLT"
    )
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "inventory-service-group")
    public void onOrderCreated(@Payload OrderCreatedEvent event, Acknowledgment ack) {
        log.info("Received OrderCreatedEvent: orderId={} items={}",
            event.getOrderId(), event.getLineItems().size());
        try {
            inventoryService.reserveForOrder(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for orderId={}: {}",
                event.getOrderId(), e.getMessage());
            throw e; // let @RetryableTopic handle retries
        }
    }
}
