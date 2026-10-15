package com.kanemart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Supplier;

public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {

    // Get all active suppliers
    List<Supplier> findByActiveTrue();

    // Search supplier by name
    List<Supplier> findByNameContainingIgnoreCase(
            String name);

    // Check whether email already exists
    boolean existsByEmail(String email);

    // Find supplier by email
    Optional<Supplier> findByEmail(String email);
}