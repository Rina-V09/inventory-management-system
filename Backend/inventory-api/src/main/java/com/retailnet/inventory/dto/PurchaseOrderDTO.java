package com.retailnet.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing a Purchase Order (PO).
 * Used to manage the procurement of stock from suppliers and track order lifecycle.
 */
@Data
public class PurchaseOrderDTO {

    /**
     * Unique identifier for the purchase order record.
     */
    private Long orderId;

    /**
     * The name of the product being ordered.
     */
    private String productName;

    /**
     * The total number of units requested from the supplier.
     */
    private Integer quantity;

    /**
     * Supplier name for quick procurement visibility.
     */
    private String supplierName;

    /**
     * Multi-line support: count of items in this PO.
     */
    private Integer itemCount;

    /**
     * Multi-line support: flattened line items (optional for UI).
     */
    private List<PurchaseOrderItemDTO> items;

    /**
     * Current state of the order (e.g., PENDING, SHIPPED, COMPLETED, CANCELLED).
     */
    private String status;

    /**
     * The timestamp when the order was officially placed in the system.
     */
    private LocalDateTime orderDate;
}