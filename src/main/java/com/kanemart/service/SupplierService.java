package com.kanemart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kanemart.model.Supplier;
import com.kanemart.repository.SupplierRepository;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // ==========================================
    // CREATE SUPPLIER
    // ==========================================

    public Supplier createSupplier(Supplier supplier) {

        if (supplier.getName() == null ||
                supplier.getName().isBlank()) {

            throw new RuntimeException(
                    "Supplier name is required"
            );
        }

        if (supplier.getEmail() == null ||
                supplier.getEmail().isBlank()) {

            throw new RuntimeException(
                    "Supplier email is required"
            );
        }

        if (supplierRepository.existsByEmail(
                supplier.getEmail())) {

            throw new RuntimeException(
                    "Supplier with this email already exists"
            );
        }

        supplier.setActive(true);

        supplier.setAverageLeadTime(0.0);

        return supplierRepository.save(supplier);
    }


    // ==========================================
    // GET ALL SUPPLIERS
    // ==========================================

    public List<Supplier> getAllSuppliers() {

        return supplierRepository.findAll();
    }


    // ==========================================
    // GET ACTIVE SUPPLIERS
    // ==========================================

    public List<Supplier> getActiveSuppliers() {

        return supplierRepository.findByActiveTrue();
    }


    // ==========================================
    // GET SUPPLIER BY ID
    // ==========================================

    public Supplier getSupplierById(Long id) {

        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Supplier not found"
                        ));
    }


    // ==========================================
    // GET SUPPLIER BY EMAIL
    // ==========================================

    public Supplier getSupplierByEmail(String email) {

        return supplierRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Supplier not found"
                        ));
    }


    // ==========================================
    // SEARCH SUPPLIER
    // ==========================================

    public List<Supplier> searchSuppliers(String name) {

        return supplierRepository
                .findByNameContainingIgnoreCase(name);
    }


    // ==========================================
    // UPDATE SUPPLIER
    // ==========================================

    public Supplier updateSupplier(
            Long id,
            Supplier updatedSupplier) {

        Supplier existingSupplier =
                getSupplierById(id);

        existingSupplier.setName(
                updatedSupplier.getName()
        );

        existingSupplier.setContact(
                updatedSupplier.getContact()
        );

        existingSupplier.setEmail(
                updatedSupplier.getEmail()
        );

        existingSupplier.setAddress(
                updatedSupplier.getAddress()
        );

        existingSupplier.setActive(
                updatedSupplier.isActive()
        );

        return supplierRepository.save(
                existingSupplier
        );
    }


    // ==========================================
    // DEACTIVATE SUPPLIER
    // ==========================================

    public void deactivateSupplier(Long id) {

        Supplier supplier =
                getSupplierById(id);

        supplier.setActive(false);

        supplierRepository.save(supplier);
    }
}