package com.kanemart.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.ai.ReorderRecommendationService;
import com.kanemart.ai.ReorderRecommendationService.ReorderRecommendation;

@RestController
@RequestMapping("/api/ai")
public class AiReorderController {

    private final ReorderRecommendationService reorderRecommendationService;

    public AiReorderController(
            ReorderRecommendationService reorderRecommendationService) {

        this.reorderRecommendationService =
                reorderRecommendationService;
    }

    /**
     * AI reorder recommendation.
     *
     * Example:
     * GET /api/ai/reorder/1?days=30
     */
    @GetMapping("/reorder/{productId}")
    public ResponseEntity<ReorderRecommendation> getReorderRecommendation(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int days) {

        ReorderRecommendation recommendation =
                reorderRecommendationService.getRecommendation(
                        productId,
                        days);

        return ResponseEntity.ok(recommendation);
    }

    /**
     * Returns only the recommended reorder quantity.
     *
     * Example:
     * GET /api/ai/reorder/1/quantity?days=30
     */
    @GetMapping("/reorder/{productId}/quantity")
    public ResponseEntity<Map<String, Object>> getReorderQuantity(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int days) {

        double reorderQuantity =
                reorderRecommendationService
                        .calculateReorderQuantity(
                                productId,
                                days);

        return ResponseEntity.ok(
                Map.of(
                        "productId", productId,
                        "forecastDays", days,
                        "reorderQuantity", reorderQuantity
                )
        );
    }
}