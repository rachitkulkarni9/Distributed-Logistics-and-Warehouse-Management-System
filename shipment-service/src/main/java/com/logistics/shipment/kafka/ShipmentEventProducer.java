package com.logistics.shipment.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.ShipmentCreatedEvent;
import com.logistics.common.event.ShipmentDeliveredEvent;
import com.logistics.common.event.ShipmentDispatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCreated(ShipmentCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.SHIPMENT_CREATED, event.getShipmentId().toString(), event)
            .whenComplete((r, ex) -> {
                if (ex != null) log.error("Failed to publish ShipmentCreatedEvent: {}", ex.getMessage());
                else log.info("Published ShipmentCreatedEvent: shipmentId={}", event.getShipmentId());
            });
    }

    public void publishDispatched(ShipmentDispatchedEvent event) {
        kafkaTemplate.send(KafkaTopics.SHIPMENT_DISPATCHED, event.getShipmentId().toString(), event)
            .whenComplete((r, ex) -> {
                if (ex != null) log.error("Failed to publish ShipmentDispatchedEvent: {}", ex.getMessage());
                else log.info("Published ShipmentDispatchedEvent: shipmentId={}", event.getShipmentId());
            });
    }

    public void publishDelivered(ShipmentDeliveredEvent event) {
        kafkaTemplate.send(KafkaTopics.SHIPMENT_DELIVERED, event.getShipmentId().toString(), event)
            .whenComplete((r, ex) -> {
                if (ex != null) log.error("Failed to publish ShipmentDeliveredEvent: {}", ex.getMessage());
                else log.info("Published ShipmentDeliveredEvent: shipmentId={}", event.getShipmentId());
            });
    }
}
