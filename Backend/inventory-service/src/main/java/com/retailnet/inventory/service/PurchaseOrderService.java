package com.retailnet.inventory.service;

import com.retailnet.inventory.entity.PurchaseOrder;

import java.util.List;

/**
 * Service interface for managing Purchase Orders (PO) in the RetailNet system.
 * This contract defines the operations for procurement, order tracking,
 * and historical order retrieval.
 */
public interface PurchaseOrderService {

    /**
     * Creates a new purchase order for a specific vendor/supplier.
     * This method handles the logic for grouping multiple products and their 
     * respective quantities into a single procurement record.
     *
     * @param supplierId The unique ID of the vendor fulfilling the order.
     * @param productIds A list of Product IDs to be included in the order.
     * @param quantities A list of quantities corresponding to each product ID.
     * @return The persisted PurchaseOrder entity with a system-generated ID.
     */
    PurchaseOrder createOrder(Long supplierId, List<Long> productIds, List<Integer> quantities);

    /**
     * Retrieves a specific purchase order based on its unique identifier.
     *
     * @param orderId The ID of the order to search for.
     * @return The PurchaseOrder record if found.
     */
    PurchaseOrder getOrderById(Long orderId);

    /**
     * Updates the status of an existing purchase order.
     * Used for approving or rejecting automated supply requests.
     *
     * @param orderId The ID of the order.
     * @param status The new status string to apply.
     * @return The updated PurchaseOrder.
     */
    PurchaseOrder updateOrderStatus(Long orderId, String status);

    /**
     * Fetches the complete list of all purchase orders stored in the system.
     * Typically used for administrative auditing and reporting views.
     *
     * @return A list of all historical and active PurchaseOrder records.
     */
    List<PurchaseOrder> getAllOrders();

}