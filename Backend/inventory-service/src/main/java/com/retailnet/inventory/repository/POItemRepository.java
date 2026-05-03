package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface POItemRepository extends JpaRepository<PurchaseOrderItem,Long> {
}
