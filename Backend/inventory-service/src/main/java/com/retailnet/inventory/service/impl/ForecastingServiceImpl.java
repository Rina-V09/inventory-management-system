package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.utils.LogConstant;
import com.retailnet.inventory.entity.DemandForecast;
import com.retailnet.inventory.entity.Product;
import com.retailnet.inventory.exception.BusinessException;
import com.retailnet.inventory.repository.DemandForecastRepository;
import com.retailnet.inventory.repository.ProductRepository;
import com.retailnet.inventory.repository.SaleRepository;
import com.retailnet.inventory.service.ForecastingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastingServiceImpl implements ForecastingService {

    private final SaleRepository saleRepository;
    private final DemandForecastRepository demandForecastRepository;
    private final ProductRepository productRepository;

    private static final String CLASS_NAME = "ForecastingServiceImpl";

    @Override
    public DemandForecast createForecastForProduct(Long productId) {
        final String METHOD_NAME = "createForecastForProduct";
        
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            if (productId == null) {
                throw new BusinessException("Product ID cannot be null for forecasting.");
            }

            Product product = productRepository.findById(productId).orElse(null);

            if (product == null) {
                log.error(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Product ID not found: " + productId);
                throw new BusinessException("Product with ID " + productId + " does not exist in our system.");
            }

            Long totalSalesLastMonth = saleRepository.findTotalSalesLastMonth(productId);

            long salesCount;
            if (totalSalesLastMonth == null) {
                salesCount = 0L;
            } else {
                salesCount = totalSalesLastMonth;
            }

            // Calculate predicted demand (Current Sales + 10% Growth)
            int predictedValue;
            if (salesCount == 0){
                log.warn(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Zero sales history. Setting base stock of 5.");
                predictedValue = 5; 
            } else {
                // Growth calculation: sales + 10 percent
                double growthBuffer = salesCount * 0.10;
                predictedValue = (int) Math.ceil(salesCount + growthBuffer);
            }

            DemandForecast forecast = new DemandForecast();
            forecast.setProduct(product);
            forecast.setStockKeepingUnit(product.getStockKeepingUnit());
            forecast.setPredictedDemand(predictedValue);
            forecast.setForecastPeriod(LocalDateTime.now().plusMonths(1));
            forecast.setConfidenceScore(0.85f);

            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_DOUBLE, CLASS_NAME, METHOD_NAME, "Calculated Prediction", predictedValue);
            
            return demandForecastRepository.save(forecast);

        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("An internal error occurred while generating the forecast for Product ID: " + productId);
        }
    }

    @Override
    public List<DemandForecast> generateAllForecasts() {
        final String METHOD_NAME = "generateAllForecasts";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            List<Product> products = productRepository.findAll();
            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME, "Processing " + products.size() + " products for bulk demand projection.");

            for (Product product : products) {
                try {
                    // This will calculate based on sales and save/update the forecast record
                    this.createForecastForProduct(product.getId());
                } catch (Exception e) {
                    log.error("Failed to generate forecast for product ID: {}. Error: {}", product.getId(), e.getMessage());
                    // Continue with next product even if one fails
                }
            }

            return demandForecastRepository.findAll();

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Failed to execute system-wide demand analysis cycle.");
        }
    }

    @Override
    public List<DemandForecast> getAllForecasts() {
        final String METHOD_NAME = "getAllForecasts";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            return demandForecastRepository.findAll();
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Failed to fetch forecast records from the database.");
        }
    }
}