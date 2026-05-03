package com.retailnet.inventory.dto;

import lombok.Data;

/**
 * DTO representing a line item inside a Purchase Order.
 * Kept intentionally lightweight for procurement and tracking screens.
 */
@Data
public class PurchaseOrderItemDTO {
    private String productId;
    private Integer quantityOrdered;
}

