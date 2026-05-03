package com.retailnet.inventory.mapper;

import com.retailnet.inventory.dto.ProductDTO;
import com.retailnet.inventory.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the {@link BaseMapper} for Product entities.
 * * This component handles the structural transformation between the persistence 
 * layer (Product Entity) and the presentation layer (ProductDTO). 
 * * It ensures that the database schema remains encapsulated while providing 
 * flattened data, such as supplier names, to the API consumers.
 */
@Component
public class ProductMapper implements BaseMapper<Product, ProductDTO> {

    /**
     * Transforms a Product entity to a ProductDTO for API responses.
     */
    @Override
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setStockKeepingUnit(product.getStockKeepingUnit());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setCurrentStock(product.getCurrentStock());
        dto.setReorderPoint(product.getReorderPoint());

        if (product.getSupplier() != null) {
            dto.setSupplierName(product.getSupplier().getSupplierName());
        } else {
            dto.setSupplierName("N/A");
        }

        // Calculate total sales count
        if (product.getSales() != null && !product.getSales().isEmpty()) {
            int totalSales = product.getSales().stream()
                    .mapToInt(com.retailnet.inventory.entity.Sales::getQuantity)
                    .sum();
            dto.setTotalSalesCount(totalSales);
        } else {
            dto.setTotalSalesCount(0);
        }

        return dto;
    }

    /**
     * Transforms a ProductDTO from the API into a Product entity for persistence.
     */
    @Override
    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
      
        if (dto.getId() != null) {
            product.setId(dto.getId());
        }
        
        product.setProductName(dto.getProductName());
        product.setStockKeepingUnit(dto.getStockKeepingUnit());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setCurrentStock(dto.getCurrentStock());
        product.setReorderPoint(dto.getReorderPoint());

        return product;
    }
}