package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.SupplierController;
import com.retailnet.inventory.dto.SupplierDTO;
import com.retailnet.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST Controller implementation for Supplier resources.
 * This class serves as the entry point for all vendor-related API calls.
 * It strictly communicates using DTOs to maintain a clean separation 
 * between the API contract and the internal Database schema.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SupplierControllerImpl implements SupplierController {

    private final SupplierService supplierService;

    /**
     * Endpoint to register a new supplier.
     */
    @Override
    @PostMapping(path = "/add")
    public SupplierDTO createSupplier(@RequestBody SupplierDTO supplierDTO) {
        log.info("REST request to save Supplier : {}", supplierDTO.getSupplierName());
        return supplierService.addSupplier(supplierDTO);
    }

    /**
     * Endpoint to fetch all active suppliers.
     */
    @Override
    @GetMapping(path = "/all")
    public List<SupplierDTO> getAllSuppliers() {
        log.info("REST request to get all Suppliers");
        return supplierService.findAllSuppliers();
    }

    /**
     * Endpoint to update an existing supplier's details.
     */
    @Override
    @PutMapping(path = "/update/{id}")
    public SupplierDTO updateSupplier(@PathVariable("id") Long id, @RequestBody SupplierDTO supplierDTO) {
        log.info("REST request to update Supplier ID: {}, Name: {}", id, supplierDTO.getSupplierName());
        return supplierService.updateSupplier(id, supplierDTO);
    }

    /**
     * Endpoint to remove a supplier from the system.
     */
    @Override
    @DeleteMapping(path = "/delete/{id}")
    public void deleteSupplier(@PathVariable("id") Long id) {
        log.info("REST request to delete Supplier ID: {}", id);
        supplierService.deleteSupplier(id);
    }
}