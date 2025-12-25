package com.example.demo.controller;

import com.example.demo.model.PriceAdjustmentLog;
import com.example.demo.service.PriceAdjustmentLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-adjustments")
@Tag(name = "Price Adjustments", description = "Price adjustment log endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class PriceAdjustmentLogController {

    @Autowired
    private PriceAdjustmentLogService priceAdjustmentLogService;

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get price adjustments for an event")
    public ResponseEntity<List<PriceAdjustmentLog>> getAdjustmentsByEvent(@PathVariable Long eventId) {
        List<PriceAdjustmentLog> adjustments = priceAdjustmentLogService.getAdjustmentsByEvent(eventId);
        return ResponseEntity.ok(adjustments);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all price adjustments")
    public ResponseEntity<List<PriceAdjustmentLog>> getAllAdjustments() {
        List<PriceAdjustmentLog> adjustments = priceAdjustmentLogService.getAllAdjustments();
        return ResponseEntity.ok(adjustments);
    }
}