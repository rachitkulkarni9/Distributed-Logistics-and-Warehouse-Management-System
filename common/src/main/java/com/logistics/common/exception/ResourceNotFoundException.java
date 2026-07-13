package com.logistics.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested entity does not exist in the database.
 * Maps to HTTP 404.
 */
public class ResourceNotFoundException extends LogisticsException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
            String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
        );
    }
}
