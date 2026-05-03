package com.retailnet.inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Entity representing a line item within a Purchase Order.
 * Links specific products to a procurement request with defined quantities.
 * * <p>Adheres to the {@code ID} primary key naming and {@code _FK} foreign key patterns.
 * * @see PurchaseOrder
 */
@Entity
@Table(name = "POITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    /**
     * Unique identifier for the purchase order line item.
     * Aligned with the global 'ID' naming standard for primary keys.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * Reference to the specific product code being ordered.
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * The total number of units requested for this specific line item.
     */
    @Column(name = "QUANTITY_ORDERED", nullable = false)
    private Integer quantityOrdered;

    /**
     * The parent Purchase Order that contains this line item.
     * Mapped using the 'PURCHASE_ORDER_FK' naming convention.
     */
    @ManyToOne
    @JoinColumn(name = "PURCHASE_ORDER_FK")
    @JsonBackReference
    private PurchaseOrder purchaseOrder;
}