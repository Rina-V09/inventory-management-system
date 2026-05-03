package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.CreatePurchaseOrderRequest;
import com.retailnet.inventory.dto.PurchaseOrderDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller interface for Purchase Order management operations.
 * Exposes API endpoints for procurement teams to review, approve, and modify orders.
 */
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public interface PurchaseOrderController {

    /**
     * Retrieves all purchase orders in the system.
     */
    @GetMapping("/all")
    ResponseEntity<List<PurchaseOrderDTO>> getAllOrders();

    /**
     * Retrieves a single purchase order by ID.
     */
    @GetMapping("/{id}")
    ResponseEntity<PurchaseOrderDTO> getOrderById(@PathVariable Long id);

    /**
     * Creates a new purchase order (supports multiple line items).
     * Intended for procurement team use cases.
     */
    @PostMapping("/create")
    ResponseEntity<PurchaseOrderDTO> createOrder(@Valid @RequestBody CreatePurchaseOrderRequest request);

    /**
     * Updates the status of an existing order.
     */
    @PutMapping("/{id}/status")
    ResponseEntity<PurchaseOrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status);
}
