package com.retailnet.inventory.mapper;

import com.retailnet.inventory.dto.SaleDTO;
import com.retailnet.inventory.entity.Sales;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting Sales entities to SaleDTOs.
 * This standardizes the point-of-sale data format for API consumers,
 * ensuring that internal database IDs and nested relationships are 
 * correctly flattened.
 */
@Component
public class SaleMapper implements BaseMapper<Sales, SaleDTO> {

    /**
     * Transforms a Sales entity into a SaleDTO for API responses.
     * Maps the internal auto-generated ID to saleId and extracts 
     * the product name from the associated entity.
     */
    @Override
    public SaleDTO toDTO(Sales entity) {
        if (entity == null) {
            return null;
        }

        SaleDTO dto = new SaleDTO();
        dto.setSaleId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setSaleDate(entity.getSaleDate());

        // Extract product name from nested relationship
        if (entity.getProduct() != null) {
            dto.setProductName(entity.getProduct().getProductName());
        } else {
            dto.setProductName("N/A");
        }

        return dto;
    }

    /**
     * Transforms a SaleDTO into a Sales entity for persistence.
     * Note: Typically requires a database lookup for the product reference, 
     * but provides a basic structural mapping here.
     */
    @Override
    public Sales toEntity(SaleDTO dto) {
        if (dto == null) {
            return null;
        }

        Sales entity = new Sales();
        entity.setId(dto.getSaleId());
        entity.setQuantity(dto.getQuantity());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setSaleDate(dto.getSaleDate());

        return entity;
    }
}
