package com.logistics.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base runtime exception for all logistics platform errors.
 * Carries an HTTP status so the global handler can map it correctly.
 */
public class LogisticsException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public LogisticsException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public LogisticsException(String message, HttpStatus status, String errorCode, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
