package com.kanemart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.PurchaseOrder;
import com.kanemart.model.Supplier;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPurchaseOrderNumber(
            String purchaseOrderNumber);

    List<PurchaseOrder> findBySupplier(
            Supplier supplier);

    List<PurchaseOrder> findByStatus(
            String status);
}