package com.logistics.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "storage_locations", schema = "warehouses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StorageLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false, length = 30)
    private String locationCode;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String zone = "GENERAL";

    @Column(nullable = false)
    @Builder.Default
    private int capacity = 100;

    @Column(nullable = false)
    @Builder.Default
    private int occupied = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
