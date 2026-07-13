package com.logistics.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logistics.common.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all Kafka domain events.
 * Every event carries a unique ID, type tag, correlation ID for saga tracking,
 * and a UTC timestamp.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {

    private String eventId = UUID.randomUUID().toString();

    private EventType eventType;

    /** Links all events belonging to the same saga instance. */
    private String correlationId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant occurredAt = Instant.now();

    protected BaseEvent(EventType eventType, String correlationId) {
        this.eventType = eventType;
        this.correlationId = correlationId;
    }
}
