package com.kanemart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Location;
import com.kanemart.model.Product;
import com.kanemart.model.StockTransaction;

public interface StockTransactionRepository
        extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByProduct(Product product);

    List<StockTransaction> findByLocation(Location location);

    List<StockTransaction> findByTransactionType(String transactionType);
}