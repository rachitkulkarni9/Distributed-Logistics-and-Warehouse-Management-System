package com.logistics.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a business rule is violated (e.g., cancelling a delivered order).
 * Maps to HTTP 422.
 */
public class BusinessRuleException extends LogisticsException {

    public BusinessRuleException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, String errorCode) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
    }
}
