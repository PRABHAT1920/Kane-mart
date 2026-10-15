package com.kanemart.ai;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kanemart.model.OrderItem;
import com.kanemart.repository.OrderItemRepository;

@Service
public class SalesAnalyticsService {

    private final OrderItemRepository orderItemRepository;

    public SalesAnalyticsService(
            OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public Map<LocalDate, Integer> getProductSalesTrend(
            Long productId) {

        List<OrderItem> items =
                orderItemRepository.findByProductId(productId);

        if (items == null || items.isEmpty()) {
            return new TreeMap<>();
        }

        return items.stream()
                .filter(item -> item.getProduct() != null)
                .filter(item -> item.getProduct().getId() != null)
                .filter(item -> item.getProduct().getId()
                        .equals(productId))
                .filter(item -> item.getOrder() != null)
                .filter(item -> item.getOrder().getOrderDate() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getOrder()
                                .getOrderDate()
                                .toLocalDate(),
                        TreeMap::new,
                        Collectors.summingInt(
                                OrderItem::getQuantity)));
    }

    public Map<String, Object> getSalesSummary(Long productId) {

        Map<LocalDate, Integer> sales =
                getProductSalesTrend(productId);

        int totalUnitsSold = sales.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        double averageDailySales = sales.isEmpty()
                ? 0.0
                : (double) totalUnitsSold / sales.size();

        String trend = calculateTrend(sales);

        return Map.of(
                "productId", productId,
                "totalUnitsSold", totalUnitsSold,
                "averageDailySales", averageDailySales,
                "daysWithSales", sales.size(),
                "trend", trend,
                "dailySales", sales
        );
    }

    private String calculateTrend(
            Map<LocalDate, Integer> sales) {

        if (sales.size() < 2) {
            return "INSUFFICIENT_DATA";
        }

        List<Integer> values =
                sales.values().stream().toList();

        int midpoint = values.size() / 2;

        double firstAverage = values.subList(0, midpoint)
                .stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        double secondAverage = values.subList(midpoint, values.size())
                .stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        if (secondAverage > firstAverage) {
            return "INCREASING";
        }

        if (secondAverage < firstAverage) {
            return "DECREASING";
        }

        return "STABLE";
    }
}