package com.retailnet.inventory.controller.impl;

import com.retailnet.inventory.controller.PurchaseOrderController;
import com.retailnet.inventory.dto.CreatePurchaseOrderRequest;
import com.retailnet.inventory.dto.PurchaseOrderDTO;
import com.retailnet.inventory.entity.PurchaseOrder;
import com.retailnet.inventory.mapper.PurchaseOrderMapper;
import com.retailnet.inventory.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PurchaseOrderControllerImpl implements PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public ResponseEntity<List<PurchaseOrderDTO>> getAllOrders() {
        List<PurchaseOrder> orders = purchaseOrderService.getAllOrders();
        return ResponseEntity.ok(orders.stream()
                .map(purchaseOrderMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<PurchaseOrderDTO> getOrderById(Long id) {
        PurchaseOrder order = purchaseOrderService.getOrderById(id);
        return ResponseEntity.ok(purchaseOrderMapper.toDTO(order));
    }

    @Override
    public ResponseEntity<PurchaseOrderDTO> createOrder(CreatePurchaseOrderRequest request) {
        PurchaseOrder created = purchaseOrderService.createOrder(
                request.getSupplierId(),
                request.getProductIds(),
                request.getQuantities()
        );
        return ResponseEntity.ok(purchaseOrderMapper.toDTO(created));
    }

    @Override
    public ResponseEntity<PurchaseOrderDTO> updateOrderStatus(Long id, String status) {
        PurchaseOrder updated = purchaseOrderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(purchaseOrderMapper.toDTO(updated));
    }
}
