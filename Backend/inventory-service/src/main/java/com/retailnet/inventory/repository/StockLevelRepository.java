package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLevelRepository extends JpaRepository<StockLevel,Long> {
}
