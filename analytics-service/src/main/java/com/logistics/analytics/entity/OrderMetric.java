package com.logistics.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Materialized snapshot of daily order metrics consumed from Kafka events.
 */
@Entity
@Table(name = "order_metrics", schema = "analytics",
    indexes = {@Index(name = "idx_order_metrics_date", columnList = "metricDate")})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate metricDate;

    @Column(nullable = false)
    @Builder.Default
    private long totalOrders = 0;

    @Column(nullable = false)
    @Builder.Default
    private long confirmedOrders = 0;

    @Column(nullable = false)
    @Builder.Default
    private long cancelledOrders = 0;

    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private long shipmentsCreated = 0;

    @Column(nullable = false)
    @Builder.Default
    private long shipmentsDelivered = 0;

    @CreatedDate @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
