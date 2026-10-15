package com.kanemart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.ai.ReorderRecommendationService;
import com.kanemart.ai.ReorderRecommendationService.ReorderRecommendation;
import com.kanemart.model.Inventory;
import com.kanemart.model.Product;
import com.kanemart.repository.OrderRepository;
import com.kanemart.service.InventoryService;
import com.kanemart.service.ProductService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final ReorderRecommendationService reorderRecommendationService;

    public DashboardController(
            ProductService productService,
            InventoryService inventoryService,
            OrderRepository orderRepository,
            ReorderRecommendationService reorderRecommendationService) {

        this.productService = productService;
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
        this.reorderRecommendationService =
                reorderRecommendationService;
    }

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {

        long totalProducts =
                productService.getTotalProducts();

        List<Inventory> inventory =
                inventoryService.getAllInventory();

        long availableStock =
                inventory.stream()
                        .filter(item -> item.getProduct() != null)
                        .filter(item -> item.getProduct().isActive())
                        .mapToLong(Inventory::getQuantity)
                        .sum();

        long lowStockItems =
                inventory.stream()
                        .filter(item ->
                                item.getQuantity()
                                <= item.getMinimumStock())
                        .count();

        long pendingOrders =
                orderRepository.countByStatus("PENDING");

        return Map.of(
                "totalProducts", totalProducts,
                "availableStock", availableStock,
                "lowStockItems", lowStockItems,
                "pendingOrders", pendingOrders
        );
    }

    /**
     * AI reorder recommendations for all active products.
     *
     * Forecast period: 30 days.
     */
    @GetMapping("/ai-reorders")
    public List<ReorderRecommendation> getAiReorderRecommendations() {

        List<Product> products =
                productService.getActiveProducts();

        return products.stream()
                .map(product ->
                        reorderRecommendationService
                                .getRecommendation(
                                        product.getId(),
                                        30))
                .filter(recommendation ->
                        recommendation.getReorderQuantity() > 0)
                .toList();
    }
}