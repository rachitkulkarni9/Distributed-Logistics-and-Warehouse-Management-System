package com.logistics.inventory.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.InventoryFailedEvent;
import com.logistics.common.event.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishReserved(InventoryReservedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event.getOrderId().toString(), event)
            .whenComplete((r, ex) -> {
                if (ex != null) log.error("Failed to publish InventoryReservedEvent: {}", ex.getMessage());
                else log.info("Published InventoryReservedEvent for orderId={}", event.getOrderId());
            });
    }

    public void publishFailed(InventoryFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_FAILED, event.getOrderId().toString(), event)
            .whenComplete((r, ex) -> {
                if (ex != null) log.error("Failed to publish InventoryFailedEvent: {}", ex.getMessage());
                else log.warn("Published InventoryFailedEvent for orderId={}", event.getOrderId());
            });
    }
}
