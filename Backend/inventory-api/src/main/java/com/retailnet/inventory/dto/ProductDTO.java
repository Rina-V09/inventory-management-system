package com.retailnet.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Product information.
 * This class filters out internal database details and provides a clean
 * interface for the Frontend (React/Angular) and Postman testing.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    /**
     * Standardized name field to ensure UI consistency
     */
    private String productName;

    /**
     * The SKU used for barcode scanning and tracking
     */
    private String stockKeepingUnit;

    private String category;

    private Double price;

    /**
     * Mapped from currentStock; represents current inventory for the dashboard
     */
    private Integer currentStock;

    /**
     * Mapped from reorderPoint; used for visual "Low Stock" alerts in UI
     */
    private Integer reorderPoint;

    /**
     * Flattened supplier name to avoid sending the entire Supplier object
     */
    private String supplierName;

    /**
     * Computed field for high-level sales metrics
     */
    private Integer totalSalesCount;
}