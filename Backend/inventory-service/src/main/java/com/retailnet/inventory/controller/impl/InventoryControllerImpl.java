package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.InventoryController;
import com.retailnet.inventory.dto.ProductDTO;
import com.retailnet.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implementation of InventoryController.
 * 
 * This controller handles all inventory-related operations such as:
 * - Fetching all products
 * - Saving a new product
 * - Triggering auto restock process
 */
@RestController
@RequiredArgsConstructor
public class InventoryControllerImpl implements InventoryController {

    /**
     * Service layer dependency for inventory operations.
     */
    private final InventoryService inventoryService;

    /**
     * Fetch all products from inventory.
     *
     * @return List of ProductDTO containing product details
     */
    @Override
    public List<ProductDTO> getAllProducts() {
        return inventoryService.getAllProducts();
    }

    /**
     * Save a new product into inventory.
     *
     * @param productDTO Product details received from request body
     * @return Saved ProductDTO
     */
    @Override
    public ProductDTO saveProduct(@RequestBody ProductDTO productDTO) {
        return inventoryService.saveProduct(productDTO);
    }

    /**
     * Trigger restock process.
     *
     * This API scans all products and:
     * - Checks if currentStock < reorderPoint
     * - Automatically creates Purchase Orders for such products
     *
     * @return List of messages indicating restock actions performed
     */
    @Override
    public ResponseEntity<List<String>> triggerRestock() {
        List<String> results = inventoryService.checkAndRestock();
        return ResponseEntity.ok(results);
    }
}