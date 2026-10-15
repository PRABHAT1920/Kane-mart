package com.kanemart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Supplier;
import com.kanemart.model.SupplierDelivery;

public interface SupplierDeliveryRepository
        extends JpaRepository<SupplierDelivery, Long> {

    List<SupplierDelivery> findBySupplier(
            Supplier supplier);

    List<SupplierDelivery> findBySupplierId(
            Long supplierId);
}