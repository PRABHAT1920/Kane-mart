package com.kanemart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.model.StockTransaction;
import com.kanemart.service.StockTransactionService;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockTransactionService stockTransactionService;

    public StockController(
            StockTransactionService stockTransactionService) {

        this.stockTransactionService = stockTransactionService;
    }

    // =========================
    // STOCK IN
    // =========================

    @PostMapping("/in")
    public ResponseEntity<StockTransaction> stockIn(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam int quantity,
            @RequestParam(required = false) String reference) {

        return ResponseEntity.ok(
                stockTransactionService.stockIn(
                        productId,
                        locationId,
                        quantity,
                        reference
                )
        );
    }

    // =========================
    // STOCK OUT
    // =========================

    @PostMapping("/out")
    public ResponseEntity<StockTransaction> stockOut(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam int quantity,
            @RequestParam(required = false) String reference) {

        return ResponseEntity.ok(
                stockTransactionService.stockOut(
                        productId,
                        locationId,
                        quantity,
                        reference
                )
        );
    }

    // =========================
    // STOCK TRANSFER
    // =========================

    @PostMapping("/transfer")
    public ResponseEntity<StockTransaction> transferStock(
            @RequestParam Long productId,
            @RequestParam Long fromLocationId,
            @RequestParam Long toLocationId,
            @RequestParam int quantity,
            @RequestParam(required = false) String reference) {

        return ResponseEntity.ok(
                stockTransactionService.transferStock(
                        productId,
                        fromLocationId,
                        toLocationId,
                        quantity,
                        reference
                )
        );
    }

    // =========================
    // ALL TRANSACTIONS
    // =========================

    @GetMapping("/transactions")
    public ResponseEntity<List<StockTransaction>> getAllTransactions() {

        return ResponseEntity.ok(
                stockTransactionService.getAllTransactions()
        );
    }

    // =========================
    // TRANSACTIONS BY PRODUCT
    // =========================

    @GetMapping("/transactions/product/{productId}")
    public ResponseEntity<List<StockTransaction>> getTransactionsByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                stockTransactionService
                        .getTransactionsByProduct(productId)
        );
    }
}