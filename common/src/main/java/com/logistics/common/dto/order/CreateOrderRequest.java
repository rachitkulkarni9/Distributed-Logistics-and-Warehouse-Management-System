package com.logistics.common.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderLineItemRequest> lineItems;

    @NotNull(message = "Shipping address is required")
    @Valid
    private AddressDto shippingAddress;

    private String currency = "USD";
    private String notes;
}
