package com.kanemart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Warehouse;

public interface WarehouseRepository
        extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCode(String code);

    List<Warehouse> findByActiveTrue();

    boolean existsByCode(String code);
}