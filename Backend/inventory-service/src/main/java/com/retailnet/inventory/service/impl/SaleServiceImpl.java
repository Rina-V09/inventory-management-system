package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.utils.LogConstant;
import com.retailnet.inventory.dto.SaleDTO;
import com.retailnet.inventory.entity.Product;
import com.retailnet.inventory.entity.Sales;
import com.retailnet.inventory.exception.BusinessException;
import com.retailnet.inventory.mapper.SaleMapper;
import com.retailnet.inventory.repository.ProductRepository;
import com.retailnet.inventory.repository.SaleRepository;
import com.retailnet.inventory.service.SaleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;

    private static final String CLASS_NAME = "SaleServiceImpl";

    @Override
    @Transactional 
    public void recordSale(Long productId, Integer quantity) {
        final String METHOD_NAME = "recordSale";
        
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            Product product = productRepository.findById(productId).orElse(null);

            if (product == null) {
                log.error(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Product not found");
                throw new BusinessException("Product with ID " + productId + " does not exist.");
            }

            // Check if we have enough stock in the warehouse
            if (product.getCurrentStock() < quantity) {
                log.warn(LogConstant.DEBUG_INSIDE_CLASS_METHOD_DOUBLE, CLASS_NAME, METHOD_NAME, "Insufficient stock", product.getCurrentStock());
                throw new BusinessException("Insufficient stock. Only " + product.getCurrentStock() + " units available.");
            }

            // Update Inventory (Subtracting the sold quantity)
            int updatedStock = product.getCurrentStock() - quantity;
            product.setCurrentStock(updatedStock);
            productRepository.save(product);
            
            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Stock updated for SKU: " + product.getStockKeepingUnit());

            Sales sale = new Sales();
            sale.setProduct(product);
            sale.setQuantity(quantity);
            sale.setSaleDate(LocalDateTime.now());
            
            Double price = product.getPrice() != null ? product.getPrice() : 0.0;
            sale.setTotalAmount(price * quantity);
            
            saleRepository.save(sale);
            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Sale record saved successfully");

        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("An error occurred while processing the sale for Product ID: " + productId);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<SaleDTO> getAllSales() {
        final String METHOD_NAME = "getAllSales";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);
  
        try {
            List<Sales> sales = saleRepository.findAll();
            return saleMapper.toDTOList(sales);
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Unable to fetch sales history at this time.");
        }
    }
}