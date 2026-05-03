package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.utils.LogConstant;
import com.retailnet.inventory.dto.ProductDTO;
import com.retailnet.inventory.entity.Product;
import com.retailnet.inventory.entity.PurchaseOrder;
import com.retailnet.inventory.exception.BusinessException;
import com.retailnet.inventory.mapper.ProductMapper;
import com.retailnet.inventory.repository.ProductRepository;
import com.retailnet.inventory.repository.PurchaseOrderRepository;
import com.retailnet.inventory.repository.SaleRepository;
import com.retailnet.inventory.repository.SupplierRepository;
import com.retailnet.inventory.entity.Supplier;
import com.retailnet.inventory.service.ErpIntegrationService;
import com.retailnet.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.retailnet.inventory.constant.OrderStatus.PENDING;

/**
 * Implementation of the InventoryService for RetailNet Supplies.
 * Handles core business logic for stock monitoring, automated reordering,
 * and demand-based forecasting using a DTO-first architecture.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SaleRepository saleRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;
    private final ErpIntegrationService erpIntegrationService;

    private static final String CLASS_NAME = "InventoryServiceImpl";

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        final String METHOD_NAME = "getAllProducts";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            List<Product> entities = productRepository.findAll();
            return productMapper.toDTOList(entities);
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Failed to fetch product list from database");
        }
    }

    @Override
    public List<String> checkAndRestock() {
        final String METHOD_NAME = "checkAndRestock";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            List<Product> allProducts = productRepository.findAll();
            List<String> restockDetails = new ArrayList<>();

            for (Product product : allProducts) {
                // Checking if stock level has dropped to or below reorder point
                if (product.getCurrentStock() <= product.getReorderPoint()) {
                    log.warn(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                            "Low stock for: " + product.getStockKeepingUnit());
                    restockDetails.add(createPurchaseOrder(product));
                }
            }

            if (restockDetails.isEmpty()) {
                restockDetails.add("No products are currently below their reorder point.");
            }
            return restockDetails;

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Error occurred during automated stock scan");
        }
    }

    @Override
    public String createPurchaseOrder(Product product) {
        final String METHOD_NAME = "createPurchaseOrder";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            // Check if product is null before proceeding
            if (product == null) {
                throw new BusinessException("Cannot create purchase order for null product");
            }

            if (product.getId() == null) {
                throw new BusinessException("Cannot create purchase order for unsaved product");
            }

            if (purchaseOrderRepository.existsByProductIdAndStatus(product.getId(), PENDING)) {
                log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                        "Pending PO already exists for SKU: " + product.getStockKeepingUnit());
                return "Skipped PO for " + product.getProductName() + " (SKU: " + product.getStockKeepingUnit()
                        + ") because a PENDING purchase order already exists.";
            }

            LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
            Integer totalSold = saleRepository.sumQuantityByProductAndDateAfter(product.getId(), lastMonth);

            int calculatedQuantity;
            if (totalSold != null && totalSold > 0) {
                // sold quantity + 20% buffer
                double buffer = totalSold * 0.2;
                calculatedQuantity = (int) Math.ceil(totalSold + buffer);
            } else {
                // Default restocking value
                calculatedQuantity = 50;
            }

            PurchaseOrder order = new PurchaseOrder();
            order.setProduct(product);
            order.setSupplier(product.getSupplier());
            order.setQuantity(calculatedQuantity);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(PENDING);

            PurchaseOrder saved = purchaseOrderRepository.save(order);
            erpIntegrationService.syncPurchaseOrder(saved);
            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_DOUBLE, CLASS_NAME, METHOD_NAME, "Order Created for SKU",
                    product.getStockKeepingUnit());

            return "Generated PO for " + product.getProductName() + " (SKU: " + product.getStockKeepingUnit()
                    + ") to restock " + calculatedQuantity + " units.";

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            String productName = product != null ? product.getProductName() : "unknown product";
            throw new BusinessException("Failed to create purchase order for: " + productName);
        }
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        final String METHOD_NAME = "saveProduct";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            Product productToSave = productMapper.toEntity(productDTO);
            
            if (productDTO.getSupplierName() != null && !productDTO.getSupplierName().trim().isEmpty()) {
                Supplier supplier = supplierRepository.findFirstBySupplierName(productDTO.getSupplierName());
                if (supplier != null) {
                    productToSave.setSupplier(supplier);
                }
            }

            Product savedProduct = productRepository.save(productToSave);

            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                    "Product saved successfully");

            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            String productName = productDTO != null ? productDTO.getProductName() : "unknown product";
            throw new BusinessException("Error occurred while persisting product: " + productName);
        }
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        final String METHOD_NAME = "updateProduct";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Product not found"));

            existingProduct.setProductName(productDTO.getProductName());
            existingProduct.setCategory(productDTO.getCategory());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setCurrentStock(productDTO.getCurrentStock());

            if (productDTO.getSupplierName() != null && !productDTO.getSupplierName().trim().isEmpty()) {
                Supplier supplier = supplierRepository.findFirstBySupplierName(productDTO.getSupplierName());
                if (supplier != null) {
                    existingProduct.setSupplier(supplier);
                }
            }

            Product savedProduct = productRepository.save(existingProduct);
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            String productName = productDTO != null ? productDTO.getProductName() : "unknown product";
            throw new BusinessException("Error modifying product: " + productName);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        final String METHOD_NAME = "deleteProduct";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Error deleting product ID: " + id);
        }
    }
}