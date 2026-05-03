package com.retailnet.inventory.controller;

import com.retailnet.inventory.dto.SupplierDTO;
import com.retailnet.inventory.entity.Supplier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller interface for managing Supplier lifecycle operations.
 * Facilitates the onboarding of new vendors and the retrieval of vendor master data.
 * <p>This controller manages:
 * <ul>
 * <li>Supplier registration and profile creation</li>
 * <li>Global retrieval of active supplier directories</li>
 * <li>Validation of vendor contact information and ratings</li>
 * <li>Updating and deleting donor records</li>
 * </ul>
 *
 * @see Supplier
 * @see SupplierDTO
 */
@RequestMapping("/api/suppliers")
public interface SupplierController {

    /**
     * Registers a new supplier in the RetailNet system.
     * Consumes JSON data to create a persistent supplier record.
     *
     * @param supplierDTO The data transfer object containing vendor details and ratings
     * @return The persisted {@link Supplier} entity including the generated system ID
     */
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    SupplierDTO createSupplier(@Valid @RequestBody SupplierDTO supplierDTO);

    /**
     * Retrieves a comprehensive list of all suppliers currently registered.
     * Used for populating vendor selection lists in the inventory and procurement modules.
     *
     * @return List of all {@link Supplier} entities in the database
     */
    @GetMapping(path = "/all")
    List<SupplierDTO> getAllSuppliers();

    /**
     * Updates an existing supplier's details.
     *
     * @param id The ID of the supplier to update
     * @param supplierDTO The updated supplier data
     * @return The updated {@link SupplierDTO}
     */
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    SupplierDTO updateSupplier(@PathVariable("id") Long id, @Valid @RequestBody SupplierDTO supplierDTO);

    /**
     * Deletes a supplier from the system.
     *
     * @param id The ID of the supplier to delete
     */
    @DeleteMapping(path = "/delete/{id}")
    void deleteSupplier(@PathVariable("id") Long id);

}