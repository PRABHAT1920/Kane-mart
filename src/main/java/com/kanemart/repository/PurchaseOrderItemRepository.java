package com.kanemart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.PurchaseOrder;
import com.kanemart.model.PurchaseOrderItem;

public interface PurchaseOrderItemRepository
        extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByPurchaseOrder(
            PurchaseOrder purchaseOrder);

    List<PurchaseOrderItem> findByProductId(
            Long productId);
}