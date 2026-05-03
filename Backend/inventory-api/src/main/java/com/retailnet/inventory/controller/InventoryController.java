package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.ProductDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller interface for Inventory management operations.
 * Provides endpoints for product lifecycle management, stock monitoring,
 * and automated replenishment triggers.
 * *
 * <p>
 * This controller handles:
 * <ul>
 * <li>Retrieval of complete product catalogs</li>
 * <li>Manual and automated restock process initiation</li>
 * <li>Creation and modification of product master data</li>
 * </ul>
 *
 * @see ProductDTO
 */
@RestController
@RequestMapping("/api/inventory")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:4200")
public interface InventoryController {

    /**
     * Retrieves a comprehensive list of all products currently tracked in the
     * system.
     *
     * @return List of {@link ProductDTO} containing stock levels and supplier
     *         details
     */
    @GetMapping("/products")
    List<ProductDTO> getAllProducts();

    /**
     * Manually triggers the inventory replenishment logic.
     * Evaluates current stock against reorder points to generate purchase orders.
     *
     * @return A list of messages detailing which products were restocked
     */
    @PostMapping("/trigger-restock")
    ResponseEntity<List<String>> triggerRestock();

    /**
     * Persists a new product or updates an existing product's information.
     * Validates the product data against inventory constraints before saving.
     *
     * @param productDTO The data transfer object containing product specifications
     * @return The saved {@link ProductDTO} including the generated system ID
     */
    @PostMapping("/product")
    ProductDTO saveProduct(@Valid @RequestBody ProductDTO productDTO);

}