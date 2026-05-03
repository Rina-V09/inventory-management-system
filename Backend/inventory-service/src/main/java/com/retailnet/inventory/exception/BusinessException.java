package com.retailnet.inventory.exception;

/**
 * Custom Exception for business-level errors.
 * This allows us to throw readable errors instead of technical stack traces.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}