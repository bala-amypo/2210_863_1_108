package com.example.demo.controller;

import com.example.demo.model.PriceAdjustmentLog;
import com.example.demo.service.PriceAdjustmentLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/price-adjustments")
@Tag(name = "Price Adjustment Logs")
public class PriceAdjustmentLogController {
    
    private final PriceAdjustmentLogService priceAdjustmentLogService;
    
    public PriceAdjustmentLogController(PriceAdjustmentLogService priceAdjustmentLogService) {
        this.priceAdjustmentLogService = priceAdjustmentLogService;
    }
    
    @PostMapping
    public ResponseEntity<PriceAdjustmentLog> logAdjustment(@RequestBody PriceAdjustmentLog log) {
        PriceAdjustmentLog saved = priceAdjustmentLogService.logAdjustment(log);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<PriceAdjustmentLog>> getAdjustmentsByEvent(@PathVariable Long eventId) {
        List<PriceAdjustmentLog> logs = priceAdjustmentLogService.getAdjustmentsByEvent(eventId);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping
    public ResponseEntity<List<PriceAdjustmentLog>> getAllAdjustments() {
        List<PriceAdjustmentLog> logs = priceAdjustmentLogService.getAllAdjustments();
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PriceAdjustmentLog> getAdjustmentById(@PathVariable Long id) {
        PriceAdjustmentLog log = priceAdjustmentLogService.getAdjustmentById(id);
        return ResponseEntity.ok(log);
    }
}