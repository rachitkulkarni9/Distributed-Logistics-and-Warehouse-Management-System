package com.logistics.inventory.entity;

import com.logistics.common.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_items", schema = "inventory",
    indexes = {
        @Index(name = "idx_inventory_sku", columnList = "sku", unique = true),
        @Index(name = "idx_inventory_warehouse", columnList = "warehouseId")
    })
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, length = 255)
    private String productName;

    private String description;

    @Column(nullable = false)
    private int quantityOnHand;

    @Column(nullable = false)
    @Builder.Default
    private int quantityReserved = 0;

    @Column(nullable = false)
    @Builder.Default
    private int reorderPoint = 10;

    @Column(nullable = false)
    @Builder.Default
    private int reorderQuantity = 50;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitCost;

    private String warehouseId;
    private String locationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.AVAILABLE;

    @CreatedDate @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate @Column(nullable = false)
    private Instant updatedAt;

    public int getQuantityAvailable() {
        return quantityOnHand - quantityReserved;
    }
}
