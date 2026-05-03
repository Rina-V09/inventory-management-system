package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.DemandForecastDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller interface for Demand Forecasting operations.
 * Defines the REST endpoints for generating and retrieving product demand
 * predictions.
 */
@RestController
@RequestMapping("/api/forecast")
public interface ForecastingController {

    /**
     * Triggers the calculation of a new demand forecast for a specific product.
     * * @param productId The unique ID of the product to analyze.
     * 
     * @return A ResponseEntity containing the generated DemandForecast object and
     *         HTTP 200 status.
     */
    @PostMapping("/generate/{productId}")
    ResponseEntity<DemandForecastDTO> generateProductForecast(@PathVariable("productId") Long productId);

    /**
     * Triggers a system-wide demand analysis for all active products.
     * 
     * @return A ResponseEntity containing a list of all updated DemandForecast entities.
     */
    @PostMapping("/generate-all")
    ResponseEntity<List<DemandForecastDTO>> generateAllForecasts();

    /**
     * Fetches all demand forecast records currently stored in the system.
     *
     * @return A ResponseEntity containing a list of all DemandForecast entities.
     */
    @GetMapping("/all")
    ResponseEntity<List<DemandForecastDTO>> getAllForecasts();
}