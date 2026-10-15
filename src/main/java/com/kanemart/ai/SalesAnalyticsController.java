package com.kanemart.ai;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/sales")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public SalesAnalyticsController(
            SalesAnalyticsService salesAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    @GetMapping("/trend/{productId}")
    public ResponseEntity<?> getSalesTrend(
            @PathVariable Long productId) {

        Map<String, Object> result =
                salesAnalyticsService.getSalesSummary(productId);

        return ResponseEntity.ok(result);
    }
}