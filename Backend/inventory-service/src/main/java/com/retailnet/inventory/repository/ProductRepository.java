package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // query to find products that need restocking
    List<Product> findByCurrentStockLessThanEqual(Integer reorderPoint);
}