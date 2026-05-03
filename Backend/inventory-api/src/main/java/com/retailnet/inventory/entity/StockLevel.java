package com.retailnet.inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

/**
 * Tracks the real-time granular stock status for warehouse management.
 * Provides the data required for automated replenishment and safety stock monitoring.
 * * <p>Adheres to UPPER_SNAKE_CASE naming and the {@code <REFERENCED_TABLE>_FK} pattern.
 * * @see Product
 */
@Entity
@Table(name = "STOCK_LEVEL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockLevel {

    /**
     * Unique identifier for the stock level record.
     * Aligned with the 'ID' naming convention for primary keys.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * The unique Stock Keeping Unit identifier for the inventory item.
     */
    @Column(name = "STOCK_KEEPING_UNIT", nullable = false, length = 50)
    private String stockKeepingUnit;

    /**
     * The physical quantity currently available in the warehouse.
     * Must be a non-negative value.
     */
    @Column(name = "CURRENT_QUANTITY")
    @Min(0)
    private Integer currentQuantity;

    /**
     * The inventory level at which a new purchase order should be triggered.
     */
    @Column(name = "REORDER_THRESHOLD")
    @Min(0)
    private Integer reorderThreshold;

    /**
     * The minimum level of stock maintained to mitigate risk of stockouts 
     * due to supply chain fluctuations.
     */
    @Column(name = "SAFETY_STOCK")
    private Integer safetyStock;

    /**
     * Timestamp indicating the last successful update to the inventory count.
     */
    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;

    /**
     * The specific warehouse or facility location holding this stock.
     */
    @Column(name = "LOCATION", length = 100)
    private String location;

    /**
     * The associated product master record.
     * Established as a One-to-One relationship via the 'PRODUCT_FK' constraint.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_FK")
    @JsonBackReference
    private Product product;
}