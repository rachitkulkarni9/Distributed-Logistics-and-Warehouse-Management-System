package com.logistics.shipment.entity;

import com.logistics.common.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "shipments", schema = "shipments",
    indexes = {
        @Index(name = "idx_shipment_order", columnList = "orderId"),
        @Index(name = "idx_shipment_tracking", columnList = "trackingNumber", unique = true)
    })
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false, unique = true, length = 50)
    private String trackingNumber;

    @Column(nullable = false, length = 100)
    private String carrier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.CREATED;

    private String originWarehouseId;

    @Column(columnDefinition = "TEXT")
    private String destinationAddress;

    private LocalDate estimatedDeliveryDate;
    private Instant dispatchedAt;
    private Instant deliveredAt;

    private double weightKg;
    private String dimensions;

    private String correlationId;

    @CreatedDate @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate @Column(nullable = false)
    private Instant updatedAt;
}
