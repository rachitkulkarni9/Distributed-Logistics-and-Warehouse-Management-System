package com.logistics.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when creating a resource that already exists (e.g., duplicate SKU).
 * Maps to HTTP 409.
 */
public class DuplicateResourceException extends LogisticsException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(
            String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
            HttpStatus.CONFLICT,
            "DUPLICATE_RESOURCE"
        );
    }
}
