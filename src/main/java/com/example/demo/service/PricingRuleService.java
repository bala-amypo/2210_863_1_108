package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.PricingRule;
import com.example.demo.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PricingRuleService {
    
    private final PricingRuleRepository pricingRuleRepository;
    
    public PricingRuleService(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }
    
    @Transactional
    public PricingRule createRule(PricingRule rule) {
        // Validate: Check for duplicate rule code
        if (pricingRuleRepository.existsByRuleCode(rule.getRuleCode())) {
            throw new BadRequestException("Rule code already exists");
        }
        
        // Validate: Price multiplier must be > 0
        if (rule.getPriceMultiplier() == null || rule.getPriceMultiplier() <= 0) {
            throw new BadRequestException("Price multiplier must be > 0");
        }
        
        return pricingRuleRepository.save(rule);
    }
    
    @Transactional
    public PricingRule updateRule(Long id, PricingRule updatedRule) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pricing rule not found"));
        
        // Validate: Price multiplier must be > 0
        if (updatedRule.getPriceMultiplier() != null && updatedRule.getPriceMultiplier() <= 0) {
            throw new BadRequestException("Price multiplier must be > 0");
        }
        
        if (updatedRule.getDescription() != null) {
            rule.setDescription(updatedRule.getDescription());
        }
        if (updatedRule.getMinRemainingSeats() != null) {
            rule.setMinRemainingSeats(updatedRule.getMinRemainingSeats());
        }
        if (updatedRule.getMaxRemainingSeats() != null) {
            rule.setMaxRemainingSeats(updatedRule.getMaxRemainingSeats());
        }
        if (updatedRule.getDaysBeforeEvent() != null) {
            rule.setDaysBeforeEvent(updatedRule.getDaysBeforeEvent());
        }
        if (updatedRule.getPriceMultiplier() != null) {
            rule.setPriceMultiplier(updatedRule.getPriceMultiplier());
        }
        if (updatedRule.getActive() != null) {
            rule.setActive(updatedRule.getActive());
        }
        
        return pricingRuleRepository.save(rule);
    }
    
    public List<PricingRule> getActiveRules() {
        return pricingRuleRepository.findByActiveTrue();
    }
    
    public PricingRule getRuleByCode(String ruleCode) {
        return pricingRuleRepository.findByRuleCode(ruleCode)
                .orElseThrow(() -> new NotFoundException("Pricing rule not found"));
    }
    
    public PricingRule getRuleById(Long id) {
        return pricingRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pricing rule not found"));
    }
    
    public List<PricingRule> getAllRules() {
        return pricingRuleRepository.findAll();
    }
}