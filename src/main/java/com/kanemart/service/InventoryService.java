package com.kanemart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kanemart.model.Inventory;
import com.kanemart.model.Location;
import com.kanemart.model.Product;
import com.kanemart.repository.InventoryRepository;
import com.kanemart.repository.LocationRepository;
import com.kanemart.repository.ProductRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            LocationRepository locationRepository) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found"));
    }

    public List<Inventory> getInventoryByProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return inventoryRepository.findByProduct(product);
    }

    public List<Inventory> getInventoryByLocation(Long locationId) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() ->
                        new RuntimeException("Location not found"));

        return inventoryRepository.findByLocation(location);
    }

    public Inventory addInventory(
            Long productId,
            Long locationId,
            int quantity,
            int minimumStock) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() ->
                        new RuntimeException("Location not found"));

        Inventory inventory = new Inventory(
                product,
                location,
                quantity,
                minimumStock
        );

        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(
            Long id,
            int quantity,
            int minimumStock) {

        Inventory inventory = getInventoryById(id);

        inventory.setQuantity(quantity);
        inventory.setMinimumStock(minimumStock);

        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getLowStockInventory() {

    return inventoryRepository.findAll()
            .stream()
            .filter(inventory ->
                    inventory.getQuantity()
                    <= inventory.getMinimumStock())
            .toList();
    }
}