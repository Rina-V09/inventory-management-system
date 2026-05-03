package com.retailnet.inventory.exception;

import com.retailnet.inventory.utils.LogConstant; 
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

/**
 * Centralized exception handler for the RetailNet system.
 * This class catches exceptions thrown across all controllers and 
 * transforms them into a consistent JSON response for the frontend.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String CLASS_NAME = "GlobalExceptionHandler";

    /**
     * Handles custom business logic errors (e.g., "Supplier name already exists").
     * Returns a 400 Bad Request.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        final String METHOD_NAME = "handleBusinessException";
        log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BUSINESS_LOGIC_ERROR",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles 415 Unsupported Media Type (Missing @RequestBody or wrong JSON format).
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "UNSUPPORTED_MEDIA_TYPE",
                "Please ensure you are sending 'application/json'."
        );
        return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Handles 404 Not Found (Wrong URL).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                "The requested URL was not found."
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Generic Catch-all for 500 Internal Server Errors.
     * Logs the full exception trace for debugging while showing a clean message to the user.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        final String METHOD_NAME = "handleGlobalException";
 
        log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected system error occurred. Please contact the administrator."
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}