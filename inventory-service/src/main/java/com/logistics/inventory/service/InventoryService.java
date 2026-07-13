package com.logistics.inventory.service;

import com.logistics.common.dto.inventory.InventoryDto;
import com.logistics.common.enums.InventoryStatus;
import com.logistics.common.event.InventoryFailedEvent;
import com.logistics.common.event.InventoryReservedEvent;
import com.logistics.common.event.OrderCreatedEvent;
import com.logistics.common.exception.DuplicateResourceException;
import com.logistics.common.exception.InsufficientInventoryException;
import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.inventory.entity.InventoryItem;
import com.logistics.inventory.kafka.InventoryEventProducer;
import com.logistics.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer eventProducer;

    @Cacheable(value = "inventory", key = "#sku")
    @Transactional(readOnly = true)
    public InventoryItem findBySku(String sku) {
        return inventoryRepository.findBySku(sku)
            .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", "sku", sku));
    }

    @Transactional(readOnly = true)
    public Page<InventoryItem> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    @Transactional
    public InventoryItem create(InventoryItem item) {
        if (inventoryRepository.existsBySku(item.getSku())) {
            throw new DuplicateResourceException("InventoryItem", "sku", item.getSku());
        }
        return inventoryRepository.save(item);
    }

    /**
     * Attempts to reserve stock for all line items in an order.
     * If any SKU fails, rolls back all reservations and publishes a failure event.
     */
    @Transactional
    public void reserveForOrder(OrderCreatedEvent orderEvent) {
        List<InventoryReservedEvent.ReservedItem> reserved = new ArrayList<>();
        List<InventoryFailedEvent.FailedItem> failed = new ArrayList<>();

        for (OrderCreatedEvent.OrderLineItem lineItem : orderEvent.getLineItems()) {
            inventoryRepository.findBySkuForUpdate(lineItem.getSku()).ifPresentOrElse(item -> {
                if (item.getQuantityAvailable() >= lineItem.getQuantity()) {
                    item.setQuantityReserved(item.getQuantityReserved() + lineItem.getQuantity());
                    if (item.getQuantityAvailable() == 0) {
                        item.setStatus(InventoryStatus.OUT_OF_STOCK);
                    }
                    inventoryRepository.save(item);
                    reserved.add(InventoryReservedEvent.ReservedItem.builder()
                        .sku(lineItem.getSku())
                        .quantityReserved(lineItem.getQuantity())
                        .locationCode(item.getLocationCode())
                        .build());
                } else {
                    failed.add(InventoryFailedEvent.FailedItem.builder()
                        .sku(lineItem.getSku())
                        .requestedQuantity(lineItem.getQuantity())
                        .availableQuantity(item.getQuantityAvailable())
                        .build());
                }
            }, () -> failed.add(InventoryFailedEvent.FailedItem.builder()
                .sku(lineItem.getSku()).requestedQuantity(lineItem.getQuantity()).availableQuantity(0).build()));
        }

        if (failed.isEmpty()) {
            log.info("Inventory reserved for orderId={}", orderEvent.getOrderId());
            eventProducer.publishReserved(InventoryReservedEvent.builder()
                .correlationId(orderEvent.getCorrelationId())
                .orderId(orderEvent.getOrderId())
                .reservedItems(reserved)
                .build());
        } else {
            log.warn("Inventory reservation failed for orderId={}: {} SKU(s) unavailable",
                orderEvent.getOrderId(), failed.size());
            // Compensate: release any partial reservations
            reserved.forEach(r -> inventoryRepository.findBySku(r.getSku()).ifPresent(item -> {
                item.setQuantityReserved(item.getQuantityReserved() - r.getQuantityReserved());
                inventoryRepository.save(item);
            }));
            eventProducer.publishFailed(InventoryFailedEvent.builder()
                .correlationId(orderEvent.getCorrelationId())
                .orderId(orderEvent.getOrderId())
                .reason("One or more SKUs are out of stock")
                .failedItems(failed)
                .build());
        }
    }

    public InventoryDto toDto(InventoryItem item) {
        return InventoryDto.builder()
            .id(item.getId()).sku(item.getSku()).productName(item.getProductName())
            .quantityOnHand(item.getQuantityOnHand()).quantityReserved(item.getQuantityReserved())
            .quantityAvailable(item.getQuantityAvailable()).reorderPoint(item.getReorderPoint())
            .unitCost(item.getUnitCost()).warehouseId(item.getWarehouseId())
            .locationCode(item.getLocationCode()).status(item.getStatus())
            .lastUpdatedAt(item.getUpdatedAt()).build();
    }
}
