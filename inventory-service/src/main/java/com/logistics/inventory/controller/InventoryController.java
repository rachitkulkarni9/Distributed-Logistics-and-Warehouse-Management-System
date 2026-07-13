package com.logistics.inventory.controller;

import com.logistics.common.dto.inventory.InventoryDto;
import com.logistics.common.response.ApiResponse;
import com.logistics.common.response.PagedResponse;
import com.logistics.common.util.PageUtils;
import com.logistics.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock management")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "List all inventory items")
    public ResponseEntity<ApiResponse<PagedResponse<InventoryDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var paged = inventoryService.findAll(PageRequest.of(page, size, Sort.by("sku")));
        return ResponseEntity.ok(ApiResponse.success(PageUtils.toPagedResponse(paged.map(inventoryService::toDto))));
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get inventory item by SKU")
    public ResponseEntity<ApiResponse<InventoryDto>> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.toDto(inventoryService.findBySku(sku))));
    }
}
