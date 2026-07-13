package com.logistics.warehouse.service;

import com.logistics.common.exception.ResourceNotFoundException;
import com.logistics.warehouse.entity.Warehouse;
import com.logistics.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock private WarehouseRepository warehouseRepository;
    @InjectMocks private WarehouseService warehouseService;

    @Test
    void findById_whenExists_returnsWarehouse() {
        UUID id = UUID.randomUUID();
        Warehouse w = Warehouse.builder().id(id).code("WH-01").name("Main Warehouse").build();
        when(warehouseRepository.findById(id)).thenReturn(Optional.of(w));
        assertThat(warehouseService.findById(id).getCode()).isEqualTo("WH-01");
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(warehouseRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> warehouseService.findById(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
