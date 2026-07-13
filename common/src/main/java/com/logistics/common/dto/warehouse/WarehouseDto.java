package com.logistics.common.dto.warehouse;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class WarehouseDto {

    private UUID id;
    private String code;
    private String name;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String contactEmail;
    private String contactPhone;
    private int totalCapacity;
    private int usedCapacity;
    private boolean active;
    private Instant createdAt;
}
