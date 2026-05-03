package com.retailnet.inventory.mapper;

import com.retailnet.inventory.dto.SupplierDTO;
import com.retailnet.inventory.entity.Supplier;
import org.springframework.stereotype.Component;

/**
 * This class handles the conversion between Supplier Entities and DTOs.
 * It uses the BaseMapper template to stay consistent with the Product module.
 */
@Component
public class SupplierMapper implements BaseMapper<Supplier, SupplierDTO> {

    /**
     * Converts the raw Supplier record into a DTO for the UI.
     */
    @Override
    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) return null;

        SupplierDTO dto = new SupplierDTO();
        dto.setSupplierId(supplier.getId());
        dto.setSupplierName(supplier.getSupplierName());
        dto.setContactEmail(supplier.getContactEmail());
        dto.setLeadTimeDays(supplier.getLeadTimeDays());
        dto.setRating(supplier.getRating());
        dto.setCategory(supplier.getCategory());
        
        return dto;
    }

    /**
     * API -> DATABASE
     * Converts incoming JSON data into a Supplier Entity for MySQL.
     */
    @Override
    public Supplier toEntity(SupplierDTO dto) {
        if (dto == null) return null;

        Supplier supplier = new Supplier();
        if (dto.getSupplierId() != null) {
            supplier.setId(dto.getSupplierId());
        }
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactEmail(dto.getContactEmail());
        supplier.setLeadTimeDays(dto.getLeadTimeDays());
        supplier.setRating(dto.getRating());
        supplier.setCategory(dto.getCategory());
        
        return supplier;
    }
}