package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

/**
 * API Contract for Product Management.
 * Handles the creation, retrieval, and inventory status of warehouse items.
 */


@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public interface ProductController {

    /**
     * Adds a new product to the inventory system.
     * 
     * @param productDto The product details from the request body.
     * @return The saved ProductDTO with its generated ID.
     */
    @PostMapping("/add")
    ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDto);

    /**
     * Updates an existing product.
     * 
     * @param id         The product ID.
     * @param productDto The new details.
     * @return The updated ProductDTO.
     */
    @PutMapping("/update/{id}")
    ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDto);

    /**
     * Deletes a product by ID.
     * 
     * @param id The product ID.
     * @return No content.
     */
    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Long id);

    /**
     * Fetches all products currently available in the system.
     * 
     * @return A list of all products in DTO format.
     */
    @GetMapping("/all")
    ResponseEntity<List<ProductDTO>> getAllProducts();
}