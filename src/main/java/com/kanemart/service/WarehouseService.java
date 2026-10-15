package com.kanemart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kanemart.model.Warehouse;
import com.kanemart.repository.WarehouseRepository;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public Warehouse addWarehouse(Warehouse warehouse) {

        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new RuntimeException("Warehouse with code already exists");
        }

        return warehouseRepository.save(warehouse);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByActiveTrue();
    }

    public Warehouse getWarehouseById(Long id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
    }

    public Warehouse getWarehouseByCode(String code) {

        return warehouseRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
    }

    public Warehouse updateWarehouse(
            Long id,
            Warehouse updatedWarehouse) {

        Warehouse existingWarehouse = getWarehouseById(id);

        existingWarehouse.setName(updatedWarehouse.getName());
        existingWarehouse.setCode(updatedWarehouse.getCode());
        existingWarehouse.setAddress(updatedWarehouse.getAddress());
        existingWarehouse.setActive(updatedWarehouse.isActive());

        return warehouseRepository.save(existingWarehouse);
    }

    public void deleteWarehouse(Long id) {

        Warehouse warehouse = getWarehouseById(id);

        warehouseRepository.delete(warehouse);
    }
}