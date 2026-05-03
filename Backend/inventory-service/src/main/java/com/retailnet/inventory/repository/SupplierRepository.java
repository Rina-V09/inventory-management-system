package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    Supplier findBySupplierName(String supplierName);
    
    /**
     * Safely retrieves the first matching supplier by name.
     * Prevents NonUniqueResultException if human error led to duplicate names.
     */
    Supplier findFirstBySupplierName(String supplierName);
}
