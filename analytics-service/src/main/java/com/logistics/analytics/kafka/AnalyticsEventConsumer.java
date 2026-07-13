package com.logistics.analytics.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.OrderCreatedEvent;
import com.logistics.common.event.ShipmentCreatedEvent;
import com.logistics.common.event.ShipmentDeliveredEvent;
import com.logistics.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "analytics-service-group")
    public void onOrderCreated(@Payload OrderCreatedEvent event, Acknowledgment ack) {
        analyticsService.recordOrderCreated(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.SHIPMENT_CREATED, groupId = "analytics-service-group")
    public void onShipmentCreated(@Payload ShipmentCreatedEvent event, Acknowledgment ack) {
        analyticsService.recordShipmentCreated(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.SHIPMENT_DELIVERED, groupId = "analytics-service-group")
    public void onShipmentDelivered(@Payload ShipmentDeliveredEvent event, Acknowledgment ack) {
        analyticsService.recordShipmentDelivered(event);
        ack.acknowledge();
    }
}
