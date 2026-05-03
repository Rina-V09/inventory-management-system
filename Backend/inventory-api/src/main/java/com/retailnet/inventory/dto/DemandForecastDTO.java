package com.retailnet.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Data Transfer Object for Demand Forecast information.
 * Used to transport predicted inventory needs between the service and the UI.
 */
@Data
public class DemandForecastDTO {

    /**
     * Unique identifier for the forecast record.
     */
    private Long forecastId;

    /**
     * Stock Keeping Unit (SKU) associated with this forecast.
     */
    private String stockKeepUnit;

    /**
     * The number of units predicted to be needed in the specified period.
     */
    private Integer predictedDemand;

    /**
     * The specific date and time period this forecast is valid for.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime forecastPeriod;

    /**
     * Accuracy probability of the forecast (e.g., 0.0 to 1.0).
     */
    private Float confidenceScore;

}