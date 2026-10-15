package com.kanemart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Location;
import com.kanemart.model.Warehouse;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByWarehouse(Warehouse warehouse);

    List<Location> findByWarehouseId(Long warehouseId);
}