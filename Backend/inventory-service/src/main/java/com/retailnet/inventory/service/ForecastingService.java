package com.retailnet.inventory.service;

import com.retailnet.inventory.entity.DemandForecast;
import java.util.List;

/**
 * Service interface for handling inventory demand forecasting.
 * This contract defines the methods required to predict future stock needs
 * based on historical sales data and product trends.
 */
public interface ForecastingService {

    /**
     * Generates a new demand forecast for a specific product.
     * This logic usually considers the previous month's sales to determine 
     * the predicted stock levels for the upcoming period.
     *
     * @param productId The unique identifier of the product to forecast.
     * @return A DemandForecast object containing the predicted quantity and confidence score.
     */
    DemandForecast createForecastForProduct(Long productId);

    /**
     * Triggers a system-wide demand analysis for all active products.
     * Iterates through the current inventory and generates/updates predictions
     * based on latest sales trends.
     *
     * @return A list of all updated DemandForecast entities.
     */
    List<DemandForecast> generateAllForecasts();

    /**
     * Retrieves all existing demand forecast records from the system.
     * Used by the UI to display a history of predictions across the inventory.
     *
     * @return A list of all DemandForecast entities stored in the database.
     */
    List<DemandForecast> getAllForecasts();
}