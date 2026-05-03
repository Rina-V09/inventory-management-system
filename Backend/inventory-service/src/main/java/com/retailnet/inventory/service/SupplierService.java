package com.retailnet.inventory.service;

import com.retailnet.inventory.dto.SupplierDTO;
import java.util.List;

/**
 * Interface defining the business contract for Supplier management.
 * Uses DTOs to ensure the API layer never touches raw Database Entities.
 */
public interface SupplierService {

    /**
     * Registers a new vendor in the system.
     * @param supplierDTO Data from the API request
     * @return The saved Supplier data including its generated ID
     */
    SupplierDTO addSupplier(SupplierDTO supplierDTO);

    /**
     * Retrieves all registered suppliers for the UI.
     * @return List of SupplierDTOs
     */
    List<SupplierDTO> findAllSuppliers();

    /**
     * Updates an existing supplier's details.
     *
     * @param id The ID of the supplier to update
     * @param supplierDTO The updated supplier data
     * @return The updated SupplierDTO
     */
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);

    /**
     * Deletes a supplier from the database.
     *
     * @param id The ID of the supplier to delete
     */
    void deleteSupplier(Long id);
}