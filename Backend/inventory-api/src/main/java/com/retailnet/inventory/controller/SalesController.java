package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.SaleDTO;
import com.retailnet.inventory.entity.Sales;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller interface for managing sales transactions.
 * Facilitates the recording of product sales and retrieval of historical
 * transaction data.
 * *
 * <p>
 * Key Responsibilities:
 * <ul>
 * <li>Processing point-of-sale transactions by product ID</li>
 * <li>Validation of stock availability before transaction finalization</li>
 * <li>Retrieving comprehensive sales logs for auditing and reporting</li>
 * </ul>
 *
 * @see Sales
 */

@RestController
@RequestMapping("/api/sales")
public interface SalesController {

    /**
     * Executes a sale transaction for a specific product.
     * This operation typically triggers an inventory deduction and creates a sales
     * record.
     *
     * @param productId The unique identifier of the product being sold
     * @param quantity  The number of units to be sold (must be greater than zero)
     * @return A success message confirming the transaction or an error if stock is
     *         insufficient
     */
    @PostMapping("/record")
    String makeSale(@RequestParam(name = "productId") Long productId,
            @RequestParam(name = "quantity") Integer quantity);

    /**
     * Retrieves a chronological list of all sales transactions as DTOs.
     * Maps the internal database entities to a flattened format.
     *
     * @return List of {@link SaleDTO} entities representing the transaction history
     */
    @GetMapping("/history")
    List<SaleDTO> getSalesHistory();

}