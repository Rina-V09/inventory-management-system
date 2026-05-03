package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.ProductController;
import com.retailnet.inventory.dto.ProductDTO;
import com.retailnet.inventory.service.InventoryService;
import com.retailnet.inventory.utils.LogConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductControllerImpl implements ProductController {

    private final InventoryService inventoryService;
    private static final String CLASS_NAME = "ProductControllerImpl";

    @Override
    public ResponseEntity<ProductDTO> addProduct(ProductDTO productDto) {
        final String METHOD_NAME = "addProduct";
        
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);
        ProductDTO savedProduct = inventoryService.saveProduct(productDto);
        
        log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "New product created: " + savedProduct.getProductName());
        
        return ResponseEntity.ok(savedProduct);
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        final String METHOD_NAME = "getAllProducts";
        
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        List<ProductDTO> products = inventoryService.getAllProducts();
        
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(Long id, ProductDTO productDto) {
        return ResponseEntity.ok(inventoryService.updateProduct(id, productDto));
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.ok("Product with id " + id + " deleted successfully");
    }
}