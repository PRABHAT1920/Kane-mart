package com.kanemart.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanemart.ai.DemandForecastService;

@RestController
@RequestMapping("/api/ai")
public class AiForecastController {

    private final DemandForecastService demandForecastService;

    public AiForecastController(
            DemandForecastService demandForecastService) {

        this.demandForecastService = demandForecastService;
    }

    /**
     * AI demand forecast.
     *
     * Example:
     * GET /api/ai/forecast/1?days=30
     */
    @GetMapping("/forecast/{productId}")
    public ResponseEntity<Map<String, Object>> forecastDemand(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int days) {

        double averageDailyDemand =
                demandForecastService
                        .calculateAverageDailyDemand(productId);

        double predictedDemand =
                demandForecastService
                        .forecastDemand(productId, days);

        int historicalDays =
                demandForecastService
                        .getHistoricalDemandDays(productId);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("productId", productId);
        response.put("forecastDays", days);
        response.put("historicalDemandDays", historicalDays);
        response.put("averageDailyDemand",
                Math.round(averageDailyDemand * 100.0) / 100.0);
        response.put("predictedDemand",
                Math.round(predictedDemand * 100.0) / 100.0);

        return ResponseEntity.ok(response);
    }

    /**
     * Returns historical daily demand.
     *
     * Example:
     * GET /api/ai/demand-history/1
     */
    @GetMapping("/demand-history/{productId}")
    public ResponseEntity<Map<LocalDate, Integer>> getDemandHistory(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                demandForecastService
                        .getDailyDemand(productId));
    }
}