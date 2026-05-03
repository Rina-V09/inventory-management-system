package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.PurchaseOrder;
import com.retailnet.inventory.constant.OrderStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("""
            SELECT COUNT(po) > 0
            FROM PurchaseOrder po
            WHERE po.product.id = :productId
              AND po.status = :status
            """)
    boolean existsByProductIdAndStatus(@Param("productId") Long productId, @Param("status") OrderStatus status);
}