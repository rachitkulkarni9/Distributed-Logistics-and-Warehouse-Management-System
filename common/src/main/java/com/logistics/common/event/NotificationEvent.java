package com.logistics.common.event;

import com.logistics.common.enums.EventType;
import com.logistics.common.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Generic notification request published to {@code notification-events}.
 * The Notification Service routes it to the appropriate channel (email/SMS/push).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NotificationEvent extends BaseEvent {

    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;
    private NotificationType notificationType;
    private String templateId;
    private Map<String, String> templateVariables;
    private String subject;
    private String body;

    @Builder
    public NotificationEvent(String correlationId, String recipientId, String recipientEmail,
                              String recipientPhone, NotificationType notificationType,
                              String templateId, Map<String, String> templateVariables,
                              String subject, String body) {
        super(EventType.NOTIFICATION_REQUESTED, correlationId);
        this.recipientId = recipientId;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.notificationType = notificationType;
        this.templateId = templateId;
        this.templateVariables = templateVariables;
        this.subject = subject;
        this.body = body;
    }
}
