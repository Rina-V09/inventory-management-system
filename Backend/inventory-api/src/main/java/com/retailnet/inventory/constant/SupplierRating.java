package com.retailnet.inventory.constant;

/**
 * Defines the performance evaluation categories for Suppliers.
 * These ratings influence procurement decisions, lead time expectations, 
 * and automated replenishment prioritization.
 */
public enum SupplierRating {

    /**
     * Top-tier performance with consistent delivery, high quality, 
     * and optimal pricing. Preferred for high-volume contracts.
     */
    EXCELLENT,

    /**
     * Reliable performance meeting most service level agreements (SLAs) 
     * with minor deviations in lead times.
     */
    GOOD,

    /**
     * Acceptable performance but requires periodic monitoring of 
     * quality or delivery schedules.
     */
    AVERAGE,

    /**
     * Significant issues with fulfillment or product quality. 
     * Procurement should be limited or strictly scrutinized.
     */
    POOR,

    /**
     * Initial evaluation phase or undergoing a performance audit. 
     * No new purchase orders should be triggered until cleared.
     */
    UNDER_REVIEW;
}