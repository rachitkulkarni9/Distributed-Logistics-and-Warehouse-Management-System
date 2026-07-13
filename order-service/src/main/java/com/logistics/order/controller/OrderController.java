package com.logistics.order.controller;

import com.logistics.common.dto.order.CreateOrderRequest;
import com.logistics.common.dto.order.OrderDto;
import com.logistics.common.exception.GlobalExceptionHandler;
import com.logistics.common.response.ApiResponse;
import com.logistics.common.response.PagedResponse;
import com.logistics.common.util.PageUtils;
import com.logistics.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order lifecycle management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderDto>> create(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDto dto = orderService.toDto(orderService.createOrder(request));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Order placed successfully", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.toDto(orderService.findById(id))));
    }

    @GetMapping
    @Operation(summary = "List all orders (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<OrderDto> result = PageUtils.toPagedResponse(
            orderService.findAll(pageable).map(orderService::toDto));
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderDto>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled",
            orderService.toDto(orderService.cancelOrder(id))));
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get current order status")
    public ResponseEntity<ApiResponse<String>> status(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
            orderService.findById(id).getStatus().name()));
    }
}
