package com.retailnet.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request contract for creating a multi-line Purchase Order.
 * Allows procurement to create one PO with many products/quantities.
 */
@Data
public class CreatePurchaseOrderRequest {

    @NotNull
    private Long supplierId;

    @NotEmpty
    private List<Long> productIds;

    @NotEmpty
    private List<Integer> quantities;
}

