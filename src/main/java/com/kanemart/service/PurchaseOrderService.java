package com.kanemart.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanemart.model.Product;
import com.kanemart.model.PurchaseOrder;
import com.kanemart.model.PurchaseOrderItem;
import com.kanemart.model.Supplier;
import com.kanemart.repository.ProductRepository;
import com.kanemart.repository.PurchaseOrderItemRepository;
import com.kanemart.repository.PurchaseOrderRepository;
import com.kanemart.repository.SupplierRepository;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderItemRepository purchaseOrderItemRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository) {

        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(
            Long supplierId,
            Long productId,
            int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found"));

        if (!supplier.isActive()) {
            throw new RuntimeException("Supplier is inactive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        double totalAmount = product.getPrice() * quantity;

        String purchaseOrderNumber =
                "PO-" + System.currentTimeMillis();

        PurchaseOrder purchaseOrder = new PurchaseOrder(
                purchaseOrderNumber,
                supplier,
                totalAmount,
                "PENDING",
                LocalDateTime.now()
        );

        PurchaseOrder savedPurchaseOrder =
                purchaseOrderRepository.save(purchaseOrder);

        PurchaseOrderItem item = new PurchaseOrderItem(
                savedPurchaseOrder,
                product,
                quantity,
                product.getPrice()
        );

        purchaseOrderItemRepository.save(item);

        return savedPurchaseOrder;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Purchase order not found"));
    }

    public PurchaseOrder getPurchaseOrderByNumber(
            String purchaseOrderNumber) {

        return purchaseOrderRepository
                .findByPurchaseOrderNumber(purchaseOrderNumber)
                .orElseThrow(() ->
                        new RuntimeException("Purchase order not found"));
    }

    public List<PurchaseOrder> getPurchaseOrdersBySupplier(
            Long supplierId) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found"));

        return purchaseOrderRepository.findBySupplier(supplier);
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(
            String status) {

        return purchaseOrderRepository.findByStatus(status);
    }

    public List<PurchaseOrderItem> getPurchaseOrderItems(
            Long purchaseOrderId) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrderById(purchaseOrderId);

        return purchaseOrderItemRepository
                .findByPurchaseOrder(purchaseOrder);
    }

    public PurchaseOrder updatePurchaseOrderStatus(
            Long id,
            String status) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrderById(id);

        purchaseOrder.setStatus(status);

        return purchaseOrderRepository.save(purchaseOrder);
    }
}