package com.retailnet.inventory.constant;

/**
 * Defines the lifecycle states of a Purchase Order in the RetailNet system.
 * Used for tracking procurement progress from initiation to fulfillment.
 */
public enum OrderStatus {

    /**
     * Order has been created but not yet reviewed or sent to the supplier.
     */
    PENDING,

    /**
     * Order was terminated either by the system or manually by an administrator.
     */
    CANCELLED,

    /**
     * Order has been reviewed and officially authorized for procurement.
     */
    APPROVED,

    /**
     * Items have been picked up by the carrier and are in transit from the supplier.
     */
    SHIPPED,

    /**
     * Items have been received at the warehouse and inventory levels have been updated.
     */
    DELIVERED
}