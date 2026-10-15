package com.kanemart.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanemart.model.Inventory;
import com.kanemart.model.Location;
import com.kanemart.model.Product;
import com.kanemart.model.StockTransaction;
import com.kanemart.repository.InventoryRepository;
import com.kanemart.repository.LocationRepository;
import com.kanemart.repository.ProductRepository;
import com.kanemart.repository.StockTransactionRepository;

@Service
public class StockTransactionService {

    private final StockTransactionRepository transactionRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    public StockTransactionService(
            StockTransactionRepository transactionRepository,
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            LocationRepository locationRepository) {

        this.transactionRepository = transactionRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
    }

    // =========================
    // STOCK IN
    // =========================

    @Transactional
    public StockTransaction stockIn(
            Long productId,
            Long locationId,
            int quantity,
            String reference) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() ->
                        new RuntimeException("Location not found"));

        Inventory inventory = inventoryRepository
                .findByProductAndLocation(product, location)
                .orElseGet(() ->
                        new Inventory(product, location, 0, 0));

        inventory.setQuantity(
                inventory.getQuantity() + quantity
        );

        inventoryRepository.save(inventory);

        StockTransaction transaction = new StockTransaction(
                product,
                location,
                quantity,
                "STOCK_IN",
                LocalDateTime.now(),
                reference
        );

        return transactionRepository.save(transaction);
    }

    // =========================
    // STOCK OUT
    // =========================

    @Transactional
    public StockTransaction stockOut(
            Long productId,
            Long locationId,
            int quantity,
            String reference) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() ->
                        new RuntimeException("Location not found"));

        Inventory inventory = inventoryRepository
                .findByProductAndLocation(product, location)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found"));

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient stock. Available stock: "
                    + inventory.getQuantity());
        }

        inventory.setQuantity(
                inventory.getQuantity() - quantity
        );

        inventoryRepository.save(inventory);

        StockTransaction transaction = new StockTransaction(
                product,
                location,
                quantity,
                "STOCK_OUT",
                LocalDateTime.now(),
                reference
        );

        return transactionRepository.save(transaction);
    }

    // =========================
    // TRANSFER STOCK
    // =========================

    @Transactional
    public StockTransaction transferStock(
            Long productId,
            Long fromLocationId,
            Long toLocationId,
            int quantity,
            String reference) {

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        if (fromLocationId.equals(toLocationId)) {
            throw new RuntimeException(
                    "Source and destination locations must be different");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Location fromLocation = locationRepository
                .findById(fromLocationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Source location not found"));

        Location toLocation = locationRepository
                .findById(toLocationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Destination location not found"));

        Inventory sourceInventory = inventoryRepository
                .findByProductAndLocation(product, fromLocation)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Source inventory not found"));

        if (sourceInventory.getQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient stock. Available stock: "
                    + sourceInventory.getQuantity());
        }

        Inventory destinationInventory = inventoryRepository
                .findByProductAndLocation(product, toLocation)
                .orElseGet(() ->
                        new Inventory(product, toLocation, 0, 0));

        sourceInventory.setQuantity(
                sourceInventory.getQuantity() - quantity
        );

        destinationInventory.setQuantity(
                destinationInventory.getQuantity() + quantity
        );

        inventoryRepository.save(sourceInventory);
        inventoryRepository.save(destinationInventory);

        StockTransaction transaction = new StockTransaction(
                product,
                toLocation,
                quantity,
                "TRANSFER",
                LocalDateTime.now(),
                reference
        );

        return transactionRepository.save(transaction);
    }

    // =========================
    // GET ALL TRANSACTIONS
    // =========================

    @Transactional(readOnly = true)
    public List<StockTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // =========================
    // GET TRANSACTIONS BY PRODUCT
    // =========================

    @Transactional(readOnly = true)
    public List<StockTransaction> getTransactionsByProduct(
            Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return transactionRepository
                .findByProduct(product);
    }
}