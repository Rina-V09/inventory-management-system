package com.retailnet.inventory.repository;

import com.retailnet.inventory.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository interface for Sales entity.
 * Handles database operations for transaction history and demand tracking.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sales, Long> {

    /**
     * Calculates the sum of sold quantities for a specific product after a given date.
     * This is used to feed the demand forecasting algorithm (10% and 20% buffers).
     *
     * @param productId Unique identifier of the product.
     * @param startDate The cutoff date (e.g., 30 days ago).
     * @return Total quantity sold as an Integer, or null if no sales found.
     */
    @Query("SELECT SUM(s.quantity) FROM Sales s WHERE s.product.id = :productId AND s.saleDate > :startDate")
    Integer sumQuantityByProductAndDateAfter(@Param("productId") Long productId, @Param("startDate") LocalDateTime startDate);

    /**
     * Helper method to retrieve total sales for the previous month.
     * Reuses the sumQuantity query to maintain the "No Mixing" logic rule.
     *
     * @param productId Unique identifier of the product.
     * @return Total sales count as a Long (defaults to 0 if null).
     */
    default Long findTotalSalesLastMonth(Long productId) {
        // Calculate the date exactly one month ago from the current timestamp
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusMonths(1);
        
        // Execute the sum query
        Integer total = sumQuantityByProductAndDateAfter(productId, thirtyDaysAgo);
        
        // Return 0L if no sales records exist to avoid NullPointerExceptions in Service logic
        return total != null ? total.longValue() : 0L;
    }
}