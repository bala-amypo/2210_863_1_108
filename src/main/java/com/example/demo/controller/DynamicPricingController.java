package com.example.demo.controller;

import com.example.demo.model.DynamicPriceRecord;
import com.example.demo.service.DynamicPricingEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dynamic-pricing")
@Tag(name = "Dynamic Pricing", description = "Dynamic pricing engine endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DynamicPricingController {

    @Autowired
    private DynamicPricingEngineService dynamicPricingEngineService;

    @PostMapping("/compute/{eventId}")
    @Operation(summary = "Compute dynamic price for an event")
    public ResponseEntity<DynamicPriceRecord> computeDynamicPrice(@PathVariable Long eventId) {
        DynamicPriceRecord priceRecord = dynamicPricingEngineService.computeDynamicPrice(eventId);
        return ResponseEntity.ok(priceRecord);
    }

    @GetMapping("/history/{eventId}")
    @Operation(summary = "Get price history for an event")
    public ResponseEntity<List<DynamicPriceRecord>> getPriceHistory(@PathVariable Long eventId) {
        List<DynamicPriceRecord> history = dynamicPricingEngineService.getPriceHistory(eventId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all computed prices")
    public ResponseEntity<List<DynamicPriceRecord>> getAllComputedPrices() {
        List<DynamicPriceRecord> prices = dynamicPricingEngineService.getAllComputedPrices();
        return ResponseEntity.ok(prices);
    }
}