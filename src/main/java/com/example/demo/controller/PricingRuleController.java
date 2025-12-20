package com.example.demo.controller;

import com.example.demo.model.PricingRule;
import com.example.demo.service.PricingRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
@Tag(name = "Pricing Rule Management")
public class PricingRuleController {
    
    private final PricingRuleService pricingRuleService;
    
    public PricingRuleController(PricingRuleService pricingRuleService) {
        this.pricingRuleService = pricingRuleService;
    }
    
    @PostMapping
    public ResponseEntity<PricingRule> createRule(@RequestBody PricingRule rule) {
        PricingRule created = pricingRuleService.createRule(rule);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PricingRule> updateRule(
            @PathVariable Long id,
            @RequestBody PricingRule updatedRule) {
        PricingRule updated = pricingRuleService.updateRule(id, updatedRule);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<PricingRule>> getActiveRules() {
        List<PricingRule> rules = pricingRuleService.getActiveRules();
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PricingRule> getRule(@PathVariable Long id) {
        PricingRule rule = pricingRuleService.getRuleById(id);
        return ResponseEntity.ok(rule);
    }
    
    @GetMapping
    public ResponseEntity<List<PricingRule>> getAllRules() {
        List<PricingRule> rules = pricingRuleService.getAllRules();
        return ResponseEntity.ok(rules);
    }
}