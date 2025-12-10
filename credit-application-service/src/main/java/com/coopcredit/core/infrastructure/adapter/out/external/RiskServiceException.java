package com.coopcredit.core.infrastructure.adapter.out.external;

/**
 * Exception thrown when communication with the Risk Service fails.
 */
public class RiskServiceException extends RuntimeException {

    public RiskServiceException(String message) {
        super(message);
    }

    public RiskServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
