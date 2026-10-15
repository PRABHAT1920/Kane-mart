package com.kanemart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Inventory;
import com.kanemart.model.Location;
import com.kanemart.model.Product;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductAndLocation(
            Product product,
            Location location
    );

    List<Inventory> findByProduct(Product product);

    List<Inventory> findByLocation(Location location);

    List<Inventory> findByQuantityLessThanEqual(int quantity);
}