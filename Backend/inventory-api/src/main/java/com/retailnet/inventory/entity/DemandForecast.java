package com.retailnet.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

/**
 * Stores predictive analytics data regarding future product demand.
 * This entity is used by the forecasting engine to help with automated replenishment.
 * * @see Product
 */
@Entity
@Table(name = "DEMAND_FORECAST")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandForecast {

    /**
     * Unique identifier for the forecast record.
     * Aligned with the 'ID' naming convention for primary keys.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") 
    private Long id;

    /**
     * The specific Stock Keeping Unit (SKU) associated with this demand prediction.
     */
    @Column(name = "STOCK_KEEPING_UNIT", length = 50)
    private String stockKeepingUnit;

    /**
     * The projected number of units expected to be sold or required.
     */
    @Column(name = "PREDICTED_DEMAND")
    private Integer predictedDemand;

    /**
     * The timestamp representing the start or midpoint of the forecasted duration.
     */
    @Column(name = "FORECAST_PERIOD")
    private LocalDateTime forecastPeriod;

    /**
     * Statistical probability of the forecast's accuracy (0.0 to 1.0).
     */
    @Column(name = "CONFIDENCE_SCORE")
    private Float confidenceScore;

    /**
     * The associated product master record.
     * Mapped using the 'PRODUCT_FK' naming convention for foreign keys.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_FK")
    @JsonBackReference
    private Product product;
}