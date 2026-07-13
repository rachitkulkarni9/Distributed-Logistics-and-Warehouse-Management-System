package com.logistics.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouses", schema = "warehouses",
    indexes = {@Index(name = "idx_warehouse_code", columnList = "code", unique = true)})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String contactEmail;
    private String contactPhone;

    @Column(nullable = false)
    @Builder.Default
    private int totalCapacity = 1000;

    @Column(nullable = false)
    @Builder.Default
    private int usedCapacity = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StorageLocation> locations = new ArrayList<>();

    @CreatedDate @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate @Column(nullable = false)
    private Instant updatedAt;
}
