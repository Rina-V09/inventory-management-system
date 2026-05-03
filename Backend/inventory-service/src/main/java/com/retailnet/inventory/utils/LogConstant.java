package com.retailnet.inventory.utils;

/**
 * Global Constants for standardized logging across the Inventory system.
 * These patterns ensure that logs are searchable by Class and Method names.
 */
public class LogConstant {

    // Used for catching errors in the GlobalExceptionHandler and Service catch
    // blocks
    public static final String EXCEPTION = "Exception occurred in class: {} | method: {} | error: {}";

    // Used at the start of every service method
    public static final String INSIDE_CLASS_METHOD = "Entering class: {} | method: {}";

    // Used when you want to log one specific variable (e.g., a Product ID)
    public static final String DEBUG_INSIDE_CLASS_METHOD_SINGLE = "Inside class: {} | method: {} | status: {}";

    // Used when you want to log a key and a value (e.g., "Quantity", 50)
    public static final String DEBUG_INSIDE_CLASS_METHOD_DOUBLE = "Inside class: {} | method: {} | {} : {}";

    // Standard tracing constants
    public static final String BEGIN = "Entering {}.{} with arguments: {}";
    public static final String END = "Exiting {}.{}";

    private LogConstant() {
        // Private constructor to prevent instantiation of this utility class
    }
}