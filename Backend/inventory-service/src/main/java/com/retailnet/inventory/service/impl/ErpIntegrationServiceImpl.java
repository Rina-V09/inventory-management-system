package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.entity.PurchaseOrder;
import com.retailnet.inventory.service.ErpIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ErpIntegrationServiceImpl implements ErpIntegrationService {

    @Override
    public void syncPurchaseOrder(PurchaseOrder order) {
        if (order == null || order.getId() == null) {
            log.warn("Attempted to sync a null or unsaved Purchase Order to ERP.");
            return;
        }
        
        log.info("Simulating ERP Integration... Syncing Purchase Order ID: {} to external ERP System.", order.getId());
        
        try {
            // Simulated network delay to external ERP System
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ERP sync was interrupted");
        }
        
        log.info("Successfully synchronized Purchase Order ID: {} with ERP.", order.getId());
    }
}
