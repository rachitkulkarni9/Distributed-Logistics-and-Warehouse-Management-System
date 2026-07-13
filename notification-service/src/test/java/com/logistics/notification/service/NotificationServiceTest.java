package com.logistics.notification.service;

import com.logistics.common.enums.NotificationType;
import com.logistics.common.event.NotificationEvent;
import com.logistics.notification.entity.NotificationLog;
import com.logistics.notification.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationLogRepository logRepository;
    @Mock private JavaMailSender mailSender;
    @InjectMocks private NotificationService notificationService;

    @Test
    void send_emailNotification_callsMailSenderAndLogsRecord() {
        NotificationEvent event = NotificationEvent.builder()
            .correlationId("corr-1")
            .recipientId("user-1")
            .recipientEmail("user@example.com")
            .notificationType(NotificationType.EMAIL)
            .subject("Your order is on the way")
            .body("Tracking: LGS123")
            .build();

        notificationService.send(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(logRepository).save(any(NotificationLog.class));
    }
}
