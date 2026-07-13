package com.logistics.notification.kafka;

import com.logistics.common.config.KafkaTopics;
import com.logistics.common.event.NotificationEvent;
import com.logistics.common.event.ShipmentDeliveredEvent;
import com.logistics.common.event.ShipmentDispatchedEvent;
import com.logistics.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.NOTIFICATION_EVENTS, groupId = "notification-service-group")
    public void onNotificationEvent(@Payload NotificationEvent event, Acknowledgment ack) {
        log.info("Received NotificationEvent: type={} recipient={}", event.getNotificationType(), event.getRecipientId());
        notificationService.send(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.SHIPMENT_DISPATCHED, groupId = "notification-service-group")
    public void onShipmentDispatched(@Payload ShipmentDispatchedEvent event, Acknowledgment ack) {
        notificationService.handleShipmentDispatched(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.SHIPMENT_DELIVERED, groupId = "notification-service-group")
    public void onShipmentDelivered(@Payload ShipmentDeliveredEvent event, Acknowledgment ack) {
        notificationService.handleShipmentDelivered(event);
        ack.acknowledge();
    }
}
