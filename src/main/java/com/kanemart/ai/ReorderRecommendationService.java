package com.kanemart.ai;

import org.springframework.stereotype.Service;

import com.kanemart.model.Product;
import com.kanemart.repository.ProductRepository;

@Service
public class ReorderRecommendationService {

    private final ProductRepository productRepository;
    private final DemandForecastService demandForecastService;

    public ReorderRecommendationService(
            ProductRepository productRepository,
            DemandForecastService demandForecastService) {

        this.productRepository = productRepository;
        this.demandForecastService = demandForecastService;
    }

    /**
     * Calculates how much stock should be reordered.
     *
     * Formula:
     *
     * Recommended Stock =
     * Predicted Demand + Reorder Level
     *
     * Reorder Quantity =
     * Recommended Stock - Current Stock
     */
    public double calculateReorderQuantity(
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

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        double predictedDemand =
                demandForecastService.forecastDemand(
                        productId,
                        forecastDays);

        double recommendedStock =
                predictedDemand
                + product.getReorderLevel();

        double currentStock =
                product.getQuantity();

        double reorderQuantity =
                recommendedStock - currentStock;

        /*
         * If enough stock is already available,
         * no reorder is required.
         */
        if (reorderQuantity <= 0) {
            return 0;
        }

        return Math.ceil(reorderQuantity);
    }

    /**
     * Returns a complete reorder recommendation.
     */
    public ReorderRecommendation getRecommendation(
            Long productId,
            int forecastDays) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        double predictedDemand =
                demandForecastService.forecastDemand(
                        productId,
                        forecastDays);

        double currentStock =
                product.getQuantity();

        double reorderLevel =
                product.getReorderLevel();

        double recommendedStock =
                predictedDemand + reorderLevel;

        double reorderQuantity =
                Math.max(
                        0,
                        Math.ceil(
                                recommendedStock
                                - currentStock));

        String status;

        if (currentStock <= reorderLevel) {
            status = "URGENT_REORDER";
        } else if (currentStock < predictedDemand) {
            status = "REORDER_RECOMMENDED";
        } else {
            status = "STOCK_SUFFICIENT";
        }

        return new ReorderRecommendation(
                product.getId(),
                product.getName(),
                product.getSku(),
                currentStock,
                predictedDemand,
                reorderLevel,
                recommendedStock,
                reorderQuantity,
                status
        );
    }

    /**
     * Response object for reorder recommendations.
     */
    public static class ReorderRecommendation {

        private final Long productId;
        private final String productName;
        private final String sku;
        private final double currentStock;
        private final double predictedDemand;
        private final double reorderLevel;
        private final double recommendedStock;
        private final double reorderQuantity;
        private final String status;

        public ReorderRecommendation(
                Long productId,
                String productName,
                String sku,
                double currentStock,
                double predictedDemand,
                double reorderLevel,
                double recommendedStock,
                double reorderQuantity,
                String status) {

            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.currentStock = currentStock;
            this.predictedDemand = predictedDemand;
            this.reorderLevel = reorderLevel;
            this.recommendedStock = recommendedStock;
            this.reorderQuantity = reorderQuantity;
            this.status = status;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public String getSku() {
            return sku;
        }

        public double getCurrentStock() {
            return currentStock;
        }

        public double getPredictedDemand() {
            return predictedDemand;
        }

        public double getReorderLevel() {
            return reorderLevel;
        }

        public double getRecommendedStock() {
            return recommendedStock;
        }

        public double getReorderQuantity() {
            return reorderQuantity;
        }

        public String getStatus() {
            return status;
        }
    }
}