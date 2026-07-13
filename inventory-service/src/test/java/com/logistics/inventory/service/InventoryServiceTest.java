package com.logistics.inventory.service;

import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.inventory.entity.InventoryItem;
import com.logistics.inventory.kafka.InventoryEventProducer;
import com.logistics.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private InventoryEventProducer eventProducer;
    @InjectMocks private InventoryService inventoryService;

    @Test
    void findBySku_whenExists_returnsItem() {
        InventoryItem item = InventoryItem.builder().sku("SKU-1").productName("Widget").build();
        when(inventoryRepository.findBySku("SKU-1")).thenReturn(Optional.of(item));
        assertThat(inventoryService.findBySku("SKU-1").getSku()).isEqualTo("SKU-1");
    }

    @Test
    void findBySku_whenMissing_throwsNotFound() {
        when(inventoryRepository.findBySku("MISSING")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> inventoryService.findBySku("MISSING"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
