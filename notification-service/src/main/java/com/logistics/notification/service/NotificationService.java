package com.logistics.notification.service;

import com.logistics.common.enums.NotificationType;
import com.logistics.common.event.NotificationEvent;
import com.logistics.common.event.ShipmentDeliveredEvent;
import com.logistics.common.event.ShipmentDispatchedEvent;
import com.logistics.notification.entity.NotificationLog;
import com.logistics.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Routes notifications to the appropriate channel and persists a log record.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository logRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void send(NotificationEvent event) {
        log.info("Sending {} notification to recipientId={}", event.getNotificationType(), event.getRecipientId());

        String error = null;
        try {
            switch (event.getNotificationType()) {
                case EMAIL -> sendEmail(event.getRecipientEmail(), event.getSubject(), event.getBody());
                case SMS   -> sendSms(event.getRecipientPhone(), event.getBody());
                default    -> log.warn("Unsupported notification type: {}", event.getNotificationType());
            }
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            error = e.getMessage();
        }

        logRepository.save(NotificationLog.builder()
            .recipientId(event.getRecipientId())
            .recipientEmail(event.getRecipientEmail())
            .recipientPhone(event.getRecipientPhone())
            .type(event.getNotificationType())
            .subject(event.getSubject())
            .body(event.getBody())
            .correlationId(event.getCorrelationId())
            .status(error == null ? "SENT" : "FAILED")
            .errorMessage(error)
            .build());
    }

    @Transactional
    public void handleShipmentDispatched(ShipmentDispatchedEvent event) {
        // TODO: look up customer email from auth-service or a customer snapshot
        log.info("Shipment dispatched notification for orderId={} tracking={}", event.getOrderId(), event.getTrackingNumber());
        // TODO: publish NotificationEvent to notification-events topic
    }

    @Transactional
    public void handleShipmentDelivered(ShipmentDeliveredEvent event) {
        log.info("Shipment delivered notification for orderId={}", event.getOrderId());
        // TODO: publish NotificationEvent to notification-events topic
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Email sent to {}", to);
    }

    private void sendSms(String phone, String body) {
        // TODO: integrate Twilio or AWS SNS
        log.info("SMS (stub) to {}: {}", phone, body);
    }
}
