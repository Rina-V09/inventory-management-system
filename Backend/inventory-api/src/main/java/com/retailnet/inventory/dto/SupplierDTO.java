package com.retailnet.inventory.dto;

import com.retailnet.inventory.constant.SupplierRating;
import lombok.Data;

/**
 * Data Transfer Object for Supplier information.
 * Used for transporting vendor master data between the service layer and the user interface.
 * * @see SupplierRating
 */
@Data
public class SupplierDTO {

    /**
     * Unique identifier for the supplier record.
     * Maps to the ID in the SUPPLIER database table.
     */
    private Long supplierId;

    /**
     * The legal or trade name of the vendor or supply organization.
     */
    private String supplierName;

    /**
     * Primary business email address used for procurement communications and order dispatch.
     */
    private String contactEmail;

    /**
     * The industry or product category associated with this supplier (e.g., Electronics, Stationery).
     */
    private String category;

    /**
     * The performance-based evaluation status of the supplier.
     * Influences automated reordering priorities.
     */
    private SupplierRating rating;

    /**
     * Estimated delivery time for orders from this supplier (in days).
     */
    private Integer leadTimeDays;

}