package com.logistics.warehouse.service;

import com.logistics.common.dto.warehouse.WarehouseDto;
import com.logistics.common.exception.DuplicateResourceException;
import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.warehouse.entity.Warehouse;
import com.logistics.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public Page<Warehouse> findAll(Pageable pageable) {
        return warehouseRepository.findAll(pageable);
    }

    @Cacheable(value = "warehouses", key = "#id")
    @Transactional(readOnly = true)
    public Warehouse findById(UUID id) {
        return warehouseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
    }

    @Transactional
    public Warehouse create(Warehouse warehouse) {
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new DuplicateResourceException("Warehouse", "code", warehouse.getCode());
        }
        return warehouseRepository.save(warehouse);
    }

    public WarehouseDto toDto(Warehouse w) {
        return WarehouseDto.builder()
            .id(w.getId()).code(w.getCode()).name(w.getName())
            .street(w.getStreet()).city(w.getCity()).state(w.getState())
            .postalCode(w.getPostalCode()).country(w.getCountry())
            .contactEmail(w.getContactEmail()).contactPhone(w.getContactPhone())
            .totalCapacity(w.getTotalCapacity()).usedCapacity(w.getUsedCapacity())
            .active(w.isActive()).createdAt(w.getCreatedAt()).build();
    }
}
