package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.utils.LogConstant;
import com.retailnet.inventory.entity.PurchaseOrder;
import com.retailnet.inventory.entity.PurchaseOrderItem;
import com.retailnet.inventory.entity.Supplier;
import com.retailnet.inventory.entity.Product;
import com.retailnet.inventory.repository.ProductRepository;
import com.retailnet.inventory.exception.BusinessException;
import com.retailnet.inventory.repository.PurchaseOrderRepository;
import com.retailnet.inventory.constant.OrderStatus;
import com.retailnet.inventory.repository.SupplierRepository;
import com.retailnet.inventory.service.PurchaseOrderService;
import com.retailnet.inventory.service.ErpIntegrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PurchaseOrderService that handles all purchase order operations.
 *
 * <p>
 * This service is responsible for:
 * </p>
 * <ul>
 * <li>Creating new purchase orders</li>
 * <li>Fetching all purchase orders</li>
 * <li>Fetching a purchase order by ID</li>
 * <li>Updating order status</li>
 * </ul>
 *
 * <p>
 * It also integrates with ERP system after order creation.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    /**
     * Repository for PurchaseOrder entity
     */
    private final PurchaseOrderRepository purchaseOrderRepository;

    /**
     * Repository for Supplier entity
     */
    private final SupplierRepository supplierRepository;

    /**
     * Repository for Product entity
     */
    private final ProductRepository productRepository;

    /**
     * Service for ERP integration
     */
    private final ErpIntegrationService erpIntegrationService;

    /**
     * Class name constant for logging
     */
    private static final String CLASS_NAME = "PurchaseOrderServiceImpl";

    /**
     * Creates a new Purchase Order.
     *
     * @param supplierId ID of the supplier
     * @param productIds List of product IDs
     * @param quantities List of quantities corresponding to products
     * @return Saved PurchaseOrder object
     * @throws BusinessException if validation fails or any error occurs
     */
    @Override
    public PurchaseOrder createOrder(Long supplierId, List<Long> productIds, List<Integer> quantities) {
        final String METHOD_NAME = "createOrder";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            if (productIds == null || productIds.isEmpty()) {
                throw new BusinessException("Product IDs must be provided.");
            }

            if (quantities == null || quantities.isEmpty()) {
                throw new BusinessException("Quantities must be provided.");
            }

            if (productIds.size() != quantities.size()) {
                throw new BusinessException("Product IDs and quantities must be the same length.");
            }

            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            if (supplier == null) {
                log.error(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                        "Supplier ID not found");
                throw new BusinessException("Supplier with ID " + supplierId + " does not exist.");
            }

            PurchaseOrder order = new PurchaseOrder();
            order.setSupplier(supplier);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDING);

            List<PurchaseOrderItem> items = new ArrayList<>();
            int totalQty = 0;
            Product firstProduct = null;

            for (int i = 0; i < productIds.size(); i++) {
                Long productId = productIds.get(i);
                Integer qty = quantities.get(i);

                if (productId == null) {
                    throw new BusinessException("Product ID cannot be null.");
                }
                if (qty == null || qty <= 0) {
                    throw new BusinessException("Quantity must be greater than 0 for product ID: " + productId);
                }

                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) {
                    throw new BusinessException("Product with ID " + productId + " does not exist.");
                }

                if (firstProduct == null) {
                    firstProduct = product;
                }

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setProductId(String.valueOf(productId));
                item.setQuantityOrdered(qty);
                item.setPurchaseOrder(order);
                items.add(item);
                totalQty += qty;
            }

            // Backwards compatibility with existing UI/DTO expectations
            order.setProduct(firstProduct);
            order.setQuantity(totalQty);
            order.setItems(items);

            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                    "Saving purchase order");
            PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

            erpIntegrationService.syncPurchaseOrder(savedOrder);
            return savedOrder;

        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Unexpected error occurred while creating order.");
        }
    }

    /**
     * Retrieves all purchase orders from the system.
     *
     * @return List of PurchaseOrder
     * @throws BusinessException if retrieval fails
     */
    @Override
    public List<PurchaseOrder> getAllOrders() {
        final String METHOD_NAME = "getAllOrders";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            return purchaseOrderRepository.findAll();

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Failed to fetch purchase orders.");
        }
    }

    /**
     * Retrieves a purchase order by its ID.
     *
     * @param orderId ID of the purchase order
     * @return PurchaseOrder object
     * @throws BusinessException if order not found or error occurs
     */
    @Override
    public PurchaseOrder getOrderById(Long orderId) {
        final String METHOD_NAME = "getOrderById";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            PurchaseOrder order = purchaseOrderRepository.findById(orderId).orElse(null);

            if (order == null) {
                log.warn(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                        "Order not found: " + orderId);
                throw new BusinessException("Order with ID " + orderId + " not found.");
            }

            return order;

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Error fetching order with ID " + orderId);
        }
    }

    /**
     * Updates the status of a purchase order.
     *
     * @param orderId ID of the order
     * @param status New status (String input)
     * @return Updated PurchaseOrder
     * @throws BusinessException if invalid status or order not found
     */
    @Override
    public PurchaseOrder updateOrderStatus(Long orderId, String status) {
        final String METHOD_NAME = "updateOrderStatus";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            PurchaseOrder order = purchaseOrderRepository.findById(orderId).orElse(null);
    
            if (order == null) {
                log.warn(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                        "Order not found: " + orderId);
                throw new BusinessException("Order with ID " + orderId + " not found.");
            }

            if (status == null || status.trim().isEmpty()) {
                throw new BusinessException("Order status cannot be null/empty.");
            }

            String upperCaseStatus = status.toUpperCase();

            OrderStatus orderStatus = OrderStatus.valueOf(upperCaseStatus);

            OrderStatus currentStatus = order.getStatus();
            if (currentStatus == null) {
                currentStatus = OrderStatus.PENDING;
            }

            // Enforce procurement lifecycle transitions:
            // PENDING -> APPROVED | CANCELLED
            // APPROVED -> SHIPPED | CANCELLED
            // SHIPPED -> DELIVERED
            // DELIVERED -> (terminal)
            // CANCELLED -> (terminal)
            boolean allowed =
                    (currentStatus == OrderStatus.PENDING && (orderStatus == OrderStatus.APPROVED || orderStatus == OrderStatus.CANCELLED)) ||
                    (currentStatus == OrderStatus.APPROVED && (orderStatus == OrderStatus.SHIPPED || orderStatus == OrderStatus.CANCELLED)) ||
                    (currentStatus == OrderStatus.SHIPPED && orderStatus == OrderStatus.DELIVERED);

            if (!allowed) {
                throw new BusinessException("Invalid status transition: " + currentStatus + " -> " + orderStatus);
            }

            order.setStatus(orderStatus);

            // If the order is delivered, update the product stock accordingly
            if (orderStatus == OrderStatus.DELIVERED) {
                Product product = order.getProduct();
                if (product != null) {
                    int newStock = product.getCurrentStock() + order.getQuantity();
                    log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_DOUBLE, CLASS_NAME, METHOD_NAME, 
                            "Incrementing stock for product ID: " + product.getId(), "New Stock: " + newStock);
                    product.setCurrentStock(newStock);
                    productRepository.save(product);
                }
            }

            return purchaseOrderRepository.save(order);

        } catch (BusinessException e) {
            throw e;

        } catch (IllegalArgumentException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, "Invalid status value");
            throw new BusinessException("Invalid order status: " + status);

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Error updating order with ID " + orderId);
        }
    }
}