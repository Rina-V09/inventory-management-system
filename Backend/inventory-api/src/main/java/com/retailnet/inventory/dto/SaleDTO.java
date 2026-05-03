package com.retailnet.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a Sales transaction.
 * Captures the point-of-sale data for inventory depletion and revenue tracking.
 */
@Data
public class SaleDTO {

    /**
     * Unique identifier for the specific sales record.
     */
    private long saleId;

    /**
     * Name of the product sold, typically mapped from the associated Product entity.
     */
    private String productName;

    /**
     * The number of units sold in this specific transaction.
     */
    private Integer quantity;

    /**
     * The timestamp indicating when the sale was finalized.
     */
    private LocalDateTime saleDate;

    /**
     * The gross revenue generated from this sale (Quantity * Unit Price).
     */
    private Double totalAmount;

    /**
     * Aggregate metric representing the total sales volume from the previous month.
     * Note: Typically used in dashboard or summary responses.
     */
    private Long findTotalSalesLastMonth;
}