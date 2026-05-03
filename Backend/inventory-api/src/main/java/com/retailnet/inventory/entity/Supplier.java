package com.retailnet.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retailnet.inventory.constant.SupplierRating;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Represents a Supplier in the RetailNet system.
 * This entity acts as the primary vendor master record, managing contact data and performance ratings.
 * * <p>Aligned with {@code ProductDTO} and {@code SupplierDTO} to ensure seamless mapping.
 * * @see SupplierRating
 * @see Product
 */
@Entity
@Table(name = "SUPPLIER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {

    /**
     * Unique identifier for the supplier record.
     * Consistently named 'ID' to align with the organization's primary key checklist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") 
    private Long id;

    /**
     * The registered business name of the supplier.
     */
    @Column(name = "SUPPLIER_NAME", length = 100, unique = true)
    private String supplierName;

    /**
     * Primary contact email for procurement and order fulfillment.
     * Enforced as unique at the database level to prevent duplicate vendor profiles.
     */
    @Column(name = "CONTACT_EMAIL", nullable = false, unique = true, length = 100)
    private String contactEmail;

    /**
     * The performance-based rating assigned to the supplier.
     * Stored as a String in the database for better readability during manual audits.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "RATING") 
    private SupplierRating rating;

    /**
     * The industry or product category associated with this supplier.
     */
    @Column(name = "CATEGORY", length = 100)
    private String category;

    /**
     * Estimated delivery time for orders from this supplier (in days).
     */
    @Column(name = "LEAD_TIME_DAYS")
    private Integer leadTimeDays;

    /**
     * The collection of products associated with this supplier.
     * Uses {@code JsonIgnore} to prevent infinite recursion during API serialization.
     */
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products;
}