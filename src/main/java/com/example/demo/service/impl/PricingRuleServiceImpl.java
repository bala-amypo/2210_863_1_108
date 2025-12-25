package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.PricingRule;
import com.example.demo.repository.PricingRuleRepository;
import com.example.demo.service.PricingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;

    @Autowired
    public PricingRuleServiceImpl(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    @Override
    public PricingRule createRule(PricingRule rule) {
        if (rule.getPriceMultiplier() == null || rule.getPriceMultiplier() <= 0) {
            throw new BadRequestException("Price multiplier must be > 0");
        }
        
        if (pricingRuleRepository.existsByRuleCode(rule.getRuleCode())) {
            throw new BadRequestException("Rule code already exists: " + rule.getRuleCode());
        }
        
        return pricingRuleRepository.save(rule);
    }

    @Override
    public PricingRule getRuleById(Long id) {
        return pricingRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pricing rule not found with id: " + id));
    }

    @Override
    public List<PricingRule> getAllRules() {
        return pricingRuleRepository.findAll();
    }

    @Override
    public List<PricingRule> getActiveRules() {
        return pricingRuleRepository.findByActiveTrue();
    }

    @Override
    public PricingRule updateRule(Long id, PricingRule rule) {
        PricingRule existing = getRuleById(id);
        
        if (rule.getPriceMultiplier() != null) {
            if (rule.getPriceMultiplier() <= 0) {
                throw new BadRequestException("Price multiplier must be > 0");
            }
            existing.setPriceMultiplier(rule.getPriceMultiplier());
        }
        
        if (rule.getMinRemainingSeats() != null) {
            existing.setMinRemainingSeats(rule.getMinRemainingSeats());
        }
        
        if (rule.getMaxRemainingSeats() != null) {
            existing.setMaxRemainingSeats(rule.getMaxRemainingSeats());
        }
        
        if (rule.getDaysBeforeEvent() != null) {
            existing.setDaysBeforeEvent(rule.getDaysBeforeEvent());
        }
        
        if (rule.getActive() != null) {
            existing.setActive(rule.getActive());
        }
        
        return pricingRuleRepository.save(existing);
    }

    @Override
    public void deleteRule(Long id) {
        if (!pricingRuleRepository.existsById(id)) {
            throw new NotFoundException("Pricing rule not found with id: " + id);
        }
        pricingRuleRepository.deleteById(id);
    }
}