package com.logistics.warehouse.controller;

import com.logistics.common.dto.warehouse.WarehouseDto;
import com.logistics.common.response.ApiResponse;
import com.logistics.common.response.PagedResponse;
import com.logistics.common.util.PageUtils;
import com.logistics.warehouse.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Warehouse and location management")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @Operation(summary = "List all warehouses")
    public ResponseEntity<ApiResponse<PagedResponse<WarehouseDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var paged = warehouseService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageUtils.toPagedResponse(paged.map(warehouseService::toDto))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<ApiResponse<WarehouseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.toDto(warehouseService.findById(id))));
    }
}
