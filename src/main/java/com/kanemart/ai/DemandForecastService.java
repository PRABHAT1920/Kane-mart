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
public class DemandForecastService {

    private final OrderItemRepository orderItemRepository;

    public DemandForecastService(
            OrderItemRepository orderItemRepository) {

        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Calculates the average quantity ordered per day.
     */
    public double calculateAverageDailyDemand(Long productId) {

        List<OrderItem> orderItems =
                orderItemRepository.findByProductId(productId);

        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }

        Map<LocalDate, Integer> dailyDemand =
                getDailyDemand(productId);

        if (dailyDemand.isEmpty()) {
            return 0;
        }

        int totalQuantity = 0;

        for (int quantity : dailyDemand.values()) {
            totalQuantity += quantity;
        }

        return (double) totalQuantity / dailyDemand.size();
    }

    /**
     * Returns daily demand for a product.
     */
    public Map<LocalDate, Integer> getDailyDemand(Long productId) {

        List<OrderItem> orderItems =
                orderItemRepository.findByProductId(productId);

        if (orderItems == null || orderItems.isEmpty()) {
            return new TreeMap<>();
        }

        return orderItems.stream()
                .filter(item -> item.getProduct() != null)
                .filter(item -> item.getProduct().getId() != null)
                .filter(item ->
                        item.getProduct().getId().equals(productId))
                .filter(item ->
                        item.getOrder() != null)
                .filter(item ->
                        item.getOrder().getOrderDate() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getOrder()
                                .getOrderDate()
                                .toLocalDate(),
                        TreeMap::new,
                        Collectors.summingInt(
                                OrderItem::getQuantity)));
    }

    /**
     * Forecasts future demand.
     */
    public double forecastDemand(
            Long productId,
            int forecastDays) {

        if (productId == null) {
            throw new IllegalArgumentException(
                    "Product ID cannot be null");
        }

        if (forecastDays <= 0) {
            throw new IllegalArgumentException(
                    "Forecast days must be greater than zero");
        }

        Map<LocalDate, Integer> dailyDemand =
                getDailyDemand(productId);

        /*
         * No historical data.
         */
        if (dailyDemand.isEmpty()) {
            return 0;
        }

        /*
         * Calculate average daily demand.
         */
        double averageDailyDemand =
                calculateAverageDailyDemand(productId);

        /*
         * Forecast future demand.
         */
        double predictedDemand =
                averageDailyDemand * forecastDays;

        return Math.max(0, predictedDemand);
    }

    /**
     * Returns the number of days for which
     * historical demand exists.
     */
    public int getHistoricalDemandDays(Long productId) {

        return getDailyDemand(productId).size();
    }
}