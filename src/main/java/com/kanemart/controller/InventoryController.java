package com.kanemart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.model.Inventory;
import com.kanemart.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(
                inventoryService.getAllInventory()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                inventoryService.getInventoryById(id)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Inventory>> getInventoryByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProduct(productId)
        );
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Inventory>> getInventoryByLocation(
            @PathVariable Long locationId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByLocation(locationId)
        );
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockInventory() {
        return ResponseEntity.ok(
                inventoryService.getLowStockInventory()
        );
    }

    @PostMapping
    public ResponseEntity<Inventory> addInventory(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam int quantity,
            @RequestParam int minimumStock) {

        return ResponseEntity.ok(
                inventoryService.addInventory(
                        productId,
                        locationId,
                        quantity,
                        minimumStock
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam int minimumStock) {

        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        id,
                        quantity,
                        minimumStock
                )
        );
    }
}