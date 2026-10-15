package com.kanemart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Order;
import com.kanemart.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all items belonging to a particular order
    List<OrderItem> findByOrder(Order order);

    // Get all order items for a particular product
    List<OrderItem> findByProductId(Long productId);

}