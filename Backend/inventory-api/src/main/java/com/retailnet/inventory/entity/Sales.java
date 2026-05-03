package com.retailnet.inventory.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import java.time.LocalDateTime;

/**
 * Entity representing the SALES table in the RetailNet inventory system.
 * Captures point-of-sale data to track inventory depletion and revenue.
 * * <p>Adheres to UPPER_SNAKE_CASE naming conventions and {@code <REFERENCED_TABLE>_FK} patterns.
 * * @see Product
 */
@Entity
@Table(name = "SALES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sales {

    /**
     * Unique identifier for the sale record.
     * Aligned with the 'ID' naming convention for primary keys across all tables.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") 
    private Long id;

    /**
     * The product involved in this sales transaction.
     * Linked via 'PRODUCT_FK' to maintain relational integrity and standard naming.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_FK") 
    @JsonBackReference
    private Product product;

    /**
     * The number of units sold during this transaction.
     */
    @Column(name = "QUANTITY")
    private Integer quantity;

    /**
     * The specific date and time when the sale was finalized.
     */
    @Column(name = "SALE_DATE")
    private LocalDateTime saleDate;

    /**
     * The total monetary value generated from this sale.
     */
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;
}