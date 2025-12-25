package com.example.demo.controller;

import com.example.demo.model.PricingRule;
import com.example.demo.service.PricingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
@Tag(name = "Pricing Rules", description = "Pricing rule management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class PricingRuleController {

    @Autowired
    private PricingRuleService pricingRuleService;

    @PostMapping
    @Operation(summary = "Create a new pricing rule")
    public ResponseEntity<PricingRule> createRule(@RequestBody PricingRule rule) {
        PricingRule created = pricingRuleService.createRule(rule);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pricing rule by ID")
    public ResponseEntity<PricingRule> getRuleById(@PathVariable Long id) {
        PricingRule rule = pricingRuleService.getRuleById(id);
        return ResponseEntity.ok(rule);
    }

    @GetMapping
    @Operation(summary = "Get all pricing rules")
    public ResponseEntity<List<PricingRule>> getAllRules() {
        List<PricingRule> rules = pricingRuleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active pricing rules")
    public ResponseEntity<List<PricingRule>> getActiveRules() {
        List<PricingRule> rules = pricingRuleService.getActiveRules();
        return ResponseEntity.ok(rules);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pricing rule")
    public ResponseEntity<PricingRule> updateRule(@PathVariable Long id, @RequestBody PricingRule rule) {
        PricingRule updated = pricingRuleService.updateRule(id, rule);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pricing rule")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        pricingRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}