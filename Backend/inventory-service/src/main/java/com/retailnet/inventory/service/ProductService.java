package com.retailnet.inventory.service;

import com.retailnet.inventory.dto.ProductDTO;
import java.util.List;

/**
 * Service interface for Product management.
 * Defines the core business operations for handling product lifecycles, 
 * stock data, and catalog retrieval.
 * * <p>This service acts as the bridge between the API Controllers 
 * and the persistent Data Access Layer.
 * * @see ProductDTO
 */
public interface ProductService {

    /**
     * Orchestrates the creation or update of a product record.
     * This includes converting the input DTO to an entity, persisting it, 
     * and returning the synchronized result.
     *
     * @param productDTO The data transfer object containing product specifications
     * @return The saved {@link ProductDTO} including the generated system ID
     */
    ProductDTO saveProduct(ProductDTO productDTO);

    /**
     * Retrieves the entire product catalog formatted for the UI.
     *
     * @return A list of {@link ProductDTO} containing current stock and pricing
     */
    List<ProductDTO> getAllProducts();

}