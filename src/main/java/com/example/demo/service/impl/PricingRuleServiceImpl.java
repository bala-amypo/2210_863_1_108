package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.PricingRule;
import com.example.demo.repository.PricingRuleRepository;
import com.example.demo.service.PricingRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PricingRuleServiceImpl implements PricingRuleService {
    
    private final PricingRuleRepository ruleRepository;
    
    public PricingRuleServiceImpl(PricingRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }
    
    @Override
    @Transactional
    public PricingRule createRule(PricingRule rule) {
        // Validate: Check for duplicate rule code
        if (ruleRepository.existsByRuleCode(rule.getRuleCode())) {
            throw new BadRequestException("Rule code already exists");
        }
        
        // Validate: Price multiplier must be > 0
        if (rule.getPriceMultiplier() == null || rule.getPriceMultiplier() <= 0) {
            throw new BadRequestException("Price multiplier must be > 0");
        }
        
        return ruleRepository.save(rule);
    }
    
    @Override
    @Transactional
    public PricingRule updateRule(Long id, PricingRule updatedRule) {
        PricingRule rule = ruleRepository.findById(id)
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
        
        return ruleRepository.save(rule);
    }
    
    @Override
    public List<PricingRule> getActiveRules() {
        return ruleRepository.findByActiveTrue();
    }
    
    @Override
    public Optional<PricingRule> getRuleByCode(String ruleCode) {
        return ruleRepository.findByRuleCode(ruleCode);
    }
    
    @Override
    public List<PricingRule> getAllRules() {
        return ruleRepository.findAll();
    }
}