package com.kanemart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kanemart.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find an order using its order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find an order using the customer's mobile number
    Optional<Order> findByCustomerMobile(String customerMobile);

    // Find orders using their status
    List<Order> findByStatus(String status);

    // Count orders by status
    long countByStatus(String status);

    // Check whether an order number already exists
    boolean existsByOrderNumber(String orderNumber);
}