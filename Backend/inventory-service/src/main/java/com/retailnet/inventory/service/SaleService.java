package com.retailnet.inventory.service;

import com.retailnet.inventory.dto.SaleDTO;
import com.retailnet.inventory.entity.Sales;

import java.util.List;

/**
 * Service interface for managing product sales and stock depletion.
 * This contract defines the operations for recording customer transactions
 * and maintaining real-time inventory accuracy.
 */
public interface SaleService {

    /**
     * Records a new sale transaction in the system.
     * This operation is responsible for decreasing the current stock level 
     * of the specified product based on the quantity sold.
     *
     * @param productId The unique identifier of the product being sold.
     * @param quantity The number of units purchased by the customer.
     */
    void recordSale(Long productId, Integer quantity);

    /**
     * Retrieves a complete history of all sales transactions as DTOs.
     * Maps the internal database entities to a flattened format.
     *
     * @return A list of SaleDTOs for API consumers.
     */
    List<SaleDTO> getAllSales();

}