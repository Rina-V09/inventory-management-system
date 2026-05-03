package com.retailnet.inventory.scheduler;

import com.retailnet.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled automation that runs the replenishment scan without manual UI action.
 * Keeps the existing manual trigger endpoint intact for demos and ad-hoc runs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoReplenishmentScheduler {

    private final InventoryService inventoryService;

    /**
     * Runs every hour.
     * Adjust in production to match business cadence and supplier lead-times.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void runAutoRestockScan() {
        try {
            var result = inventoryService.checkAndRestock();
            log.info("Auto restock scan completed. Results: {}", result);
        } catch (Exception e) {
            log.error("Auto restock scan failed: {}", e.getMessage());
        }
    }
}

