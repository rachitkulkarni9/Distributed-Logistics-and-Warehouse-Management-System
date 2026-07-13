package com.logistics.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an authenticated user lacks permission for an operation.
 * Maps to HTTP 403.
 */
public class UnauthorizedException extends LogisticsException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
