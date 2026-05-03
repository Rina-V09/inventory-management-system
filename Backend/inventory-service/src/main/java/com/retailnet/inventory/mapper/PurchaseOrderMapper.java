package com.retailnet.inventory.mapper;

import com.retailnet.inventory.dto.PurchaseOrderItemDTO;
import com.retailnet.inventory.dto.PurchaseOrderDTO;
import com.retailnet.inventory.entity.PurchaseOrder;
import com.retailnet.inventory.entity.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper implements BaseMapper<PurchaseOrder, PurchaseOrderDTO> {

    @Override
    public PurchaseOrderDTO toDTO(PurchaseOrder order) {
        if (order == null) return null;

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setOrderId(order.getId());
        if (order.getSupplier() != null) {
            dto.setSupplierName(order.getSupplier().getSupplierName());
        }

        List<PurchaseOrderItem> items = order.getItems() != null ? order.getItems() : Collections.emptyList();
        if (!items.isEmpty()) {
            dto.setItemCount(items.size());
            dto.setItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));

            int totalQty = items.stream()
                    .map(PurchaseOrderItem::getQuantityOrdered)
                    .filter(q -> q != null)
                    .mapToInt(Integer::intValue)
                    .sum();
            dto.setQuantity(totalQty);

            // Keep backwards-compatible "productName" for existing UI tables.
            if (order.getProduct() != null) {
                dto.setProductName(order.getProduct().getProductName());
            } else {
                dto.setProductName(items.size() == 1 ? "1 Item" : (items.size() + " Items"));
            }
        } else {
            // Legacy single-line PO representation
            if (order.getProduct() != null) {
                dto.setProductName(order.getProduct().getProductName());
            }
            dto.setQuantity(order.getQuantity());
            dto.setItemCount(0);
        }

        dto.setOrderDate(order.getOrderDate());
        if (order.getStatus() != null) {
            dto.setStatus(order.getStatus().name());
        }
        return dto;
    }

    private PurchaseOrderItemDTO toItemDTO(PurchaseOrderItem item) {
        PurchaseOrderItemDTO dto = new PurchaseOrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setQuantityOrdered(item.getQuantityOrdered());
        return dto;
    }

    @Override
    public PurchaseOrder toEntity(PurchaseOrderDTO dto) {
        // Not implemented extensively as POs are auto-generated or handled via specific fields
        return null;
    }
}
