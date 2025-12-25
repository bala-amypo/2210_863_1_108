package com.example.demo.service;

import com.example.demo.model.PricingRule;

import java.util.List;

public interface PricingRuleService {
    
    PricingRule createRule(PricingRule rule);
    
    PricingRule getRuleById(Long id);
    
    List<PricingRule> getAllRules();
    
    List<PricingRule> getActiveRules();
    
    PricingRule updateRule(Long id, PricingRule rule);
    
    void deleteRule(Long id);
}