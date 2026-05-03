package com.retailnet.inventory.service;

import com.retailnet.inventory.dto.ProductDTO;
import com.retailnet.inventory.entity.Product;

import java.util.List;

/**
 * Service interface for managing RetailNet Inventory operations.
 * This contract defines the methods for stock monitoring, automated replenishment,
 * and product data persistence.
 */
public interface InventoryService {

    /**
     * Retrieves all products currently available in the warehouse.
     * This is typically used by the frontend to display the master inventory list.
     *
     * @return A list of ProductDTO objects containing current stock and SKU details.
     */
    List<ProductDTO> getAllProducts();

    /**
     * Scans the entire inventory to identify items that have reached their reorder point.
     * This method is usually triggered by a scheduled background task.
     * @return A list of messages detailing which products were restocked.
     */
    List<String> checkAndRestock();

    /**
     * Generates a formal Purchase Order for a specific product.
     * This method calculates the required replenishment quantity based on sales history.
     *
     * @param product The Product entity requiring a restock.
     * @return A message detailing the generated purchase order.
     */
    String createPurchaseOrder(Product product);

    /**
     * Saves a new product or updates an existing one in the system.
     *
     * @param productDto The data transfer object containing product details from the UI.
     * @return The persisted ProductDTO including the system-generated ID.
     */
    ProductDTO saveProduct(ProductDTO productDto);

    ProductDTO updateProduct(Long id, ProductDTO productDto);

    void deleteProduct(Long id);
}