package com.kanemart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.model.PurchaseOrder;
import com.kanemart.model.PurchaseOrderItem;
import com.kanemart.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(
            PurchaseOrderService purchaseOrderService) {

        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(
            @RequestParam Long supplierId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(
                purchaseOrderService.createPurchaseOrder(
                        supplierId,
                        productId,
                        quantity
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {

        return ResponseEntity.ok(
                purchaseOrderService.getAllPurchaseOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrderById(id)
        );
    }

    @GetMapping("/number/{purchaseOrderNumber}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderByNumber(
            @PathVariable String purchaseOrderNumber) {

        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrderByNumber(
                        purchaseOrderNumber
                )
        );
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> getBySupplier(
            @PathVariable Long supplierId) {

        return ResponseEntity.ok(
                purchaseOrderService
                        .getPurchaseOrdersBySupplier(supplierId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                purchaseOrderService
                        .getPurchaseOrdersByStatus(status)
        );
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<PurchaseOrderItem>> getItems(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService
                        .getPurchaseOrderItems(id)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                purchaseOrderService
                        .updatePurchaseOrderStatus(
                                id,
                                status
                        )
        );
    }
}