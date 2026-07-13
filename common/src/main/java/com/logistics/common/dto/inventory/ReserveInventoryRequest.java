package com.logistics.common.dto.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReserveInventoryRequest {

    private UUID orderId;

    @NotEmpty(message = "At least one item is required")
    private List<ReservationItem> items;

    @Data
    public static class ReservationItem {
        @NotBlank(message = "SKU is required")
        private String sku;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}
