package com.example.demo.controller;

import com.example.demo.model.DynamicPriceRecord;
import com.example.demo.service.DynamicPricingEngineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dynamic-pricing")
@Tag(name = "Dynamic Pricing")
public class DynamicPricingController {
    
    private final DynamicPricingEngineServiceImpl dynamicPricingEngineService;
    
    public DynamicPricingController(DynamicPricingEngineServiceImpl dynamicPricingEngineService) {
        this.dynamicPricingEngineService = dynamicPricingEngineService;
    }
    
    @PostMapping("/compute/{eventId}")
    public ResponseEntity<DynamicPriceRecord> computePrice(@PathVariable Long eventId) {
        DynamicPriceRecord priceRecord = dynamicPricingEngineService.computeDynamicPrice(eventId);
        return ResponseEntity.ok(priceRecord);
    }
    
    @GetMapping("/latest/{eventId}")
    public ResponseEntity<DynamicPriceRecord> getLatestPrice(@PathVariable Long eventId) {
        DynamicPriceRecord priceRecord = dynamicPricingEngineService.getLatestPrice(eventId);
        return ResponseEntity.ok(priceRecord);
    }
    
    @GetMapping("/history/{eventId}")
    public ResponseEntity<List<DynamicPriceRecord>> getPriceHistory(@PathVariable Long eventId) {
        List<DynamicPriceRecord> history = dynamicPricingEngineService.getPriceHistory(eventId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping
    public ResponseEntity<List<DynamicPriceRecord>> getAllComputedPrices() {
        List<DynamicPriceRecord> prices = dynamicPricingEngineService.getAllComputedPrices();
        return ResponseEntity.ok(prices);
    }
}