package com.retailnet.inventory.service;

import com.retailnet.inventory.entity.PurchaseOrder;

/**
 * Service for integrating with existing ERP and supply chain management systems.
 */
public interface ErpIntegrationService {
    
    /**
     * Synchronizes a newly created Purchase Order with the external ERP.
     */
    void syncPurchaseOrder(PurchaseOrder order);
}
