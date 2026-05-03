package com.retailnet.inventory.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing a Product in the RetailNet Inventory System.
 *
 * This class is mapped to the "PRODUCT" table in the database and contains
 * all essential attributes related to a product such as name, SKU, pricing,
 * stock levels, and relationships with other entities.
 */
@Entity
@Table(name = "PRODUCT") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Product {

    /**
     * Primary key of the Product table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * Name of the product.
     * Max length restricted to 100 characters.
     */
    @Column(name = "PRODUCT_NAME", length = 100)
    private String productName;

    /**
     * Unique Stock Keeping Unit (SKU) used to identify the product.
     * Must be unique across the table.
     */
    @Column(name = "STOCK_KEEPING_UNIT", unique = true, length = 50)
    private String stockKeepingUnit;

    /**
     * Category of the product (e.g., Electronics, Grocery, etc.).
     */
    @Column(name = "CATEGORY", length = 50)
    private String category;

    /**
     * Price of the product.
     * Stored as Double for precision in monetary calculations.
     */
    @Column(name = "PRICE")
    private Double price;

    /**
     * Current available stock quantity.
     */
    @Column(name = "CURRENT_STOCK")
    private Integer currentStock;

    /**
     * Minimum threshold for stock.
     * When stock goes below this value, reordering should be triggered.
     * Validation ensures it is not negative.
     */
    @Column(name = "REORDER_POINT")
    @Min(0)
    private Integer reorderPoint;

    /**
     * Many-to-One relationship with Supplier.
     * Multiple products can be associated with a single supplier.
     *
     * FetchType.LAZY:
     * Supplier data will be loaded only when explicitly accessed.
     *
     * @JoinColumn:
     * Maps the foreign key column SUPPLIER_FK in PRODUCT table.
     *
     * @JsonBackReference:
     * Prevents infinite recursion during JSON serialization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_FK")
    @JsonBackReference
    private Supplier supplier;

    /**
     * One-to-Many relationship with Sales entity.
     * A product can have multiple sales records.
     *
     * mappedBy:
     * Refers to the "product" field in Sales entity.
     *
     * cascade = ALL:
     * All operations (persist, merge, remove, etc.) propagate to Sales.
     *
     * @JsonManagedReference:
     * Handles serialization (forward part of relationship).
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Sales> sales;

    /**
     * One-to-Many relationship with PurchaseOrder.
     * A product can be part of multiple purchase orders.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PurchaseOrder> purchaseOrder;

    /**
     * One-to-Many relationship with DemandForecast.
     * Stores predicted demand data for the product.
     *
     * orphanRemoval = true:
     * If a forecast is removed from the list, it is also deleted from DB.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DemandForecast> forecasts;

    /**
     * One-to-One relationship with StockLevel.
     * Each product has exactly one stock level record.
     *
     * mappedBy:
     * Refers to the owning side in StockLevel entity.
     *
     * orphanRemoval = true:
     * Deleting the product will also delete the associated stock level.
     */
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private StockLevel stockLevel;
}