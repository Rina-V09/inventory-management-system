package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.utils.LogConstant;
import com.retailnet.inventory.dto.ProductDTO;
import com.retailnet.inventory.entity.Product;
import com.retailnet.inventory.exception.BusinessException;
import com.retailnet.inventory.mapper.ProductMapper;
import com.retailnet.inventory.repository.ProductRepository;
import com.retailnet.inventory.repository.SupplierRepository;
import com.retailnet.inventory.entity.Supplier;
import com.retailnet.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing Product data.
 * This class coordinates the conversion between DTOs and Entities 
 * and handles communication with the ProductRepository.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    private static final String CLASS_NAME = "ProductServiceImpl";

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        final String METHOD_NAME = "saveProduct";
    
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
           if (productDTO == null) {
                throw new BusinessException("Product data cannot be null");
            }

            Product productEntity = productMapper.toEntity(productDTO);
            
            if (productDTO.getSupplierName() != null && !productDTO.getSupplierName().trim().isEmpty()) {
                Supplier supplier = supplierRepository.findBySupplierName(productDTO.getSupplierName());
                if (supplier != null) {
                    productEntity.setSupplier(supplier);
                }
            }

            Product savedProduct = productRepository.save(productEntity);
            
            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Product saved successfully with ID: " + savedProduct.getId());
            
            return productMapper.toDTO(savedProduct);

        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            String productName = productDTO != null ? productDTO.getProductName() : "Unknown";
            throw new BusinessException("An error occurred while saving the product: " + productName);
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        final String METHOD_NAME = "getAllProducts";
        
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            List<Product> products = productRepository.findAll();
            return productMapper.toDTOList(products);

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Could not retrieve the product list from the database");
        }
    }
}