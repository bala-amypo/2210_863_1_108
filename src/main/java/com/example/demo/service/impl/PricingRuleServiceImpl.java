package com.example.demo.service;

import com.example.demo.model.PricingRule;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PricingRuleServiceImpl implements PricingRuleService {

    @Override
    public PricingRule createRule(PricingRule rule) {
        return rule;
    }

    @Override
    public PricingRule updateRule(Long id, PricingRule rule) {
        rule.setId(id);
        return rule;
    }

    @Override
    public List<PricingRule> getActiveRules() {
        return new ArrayList<>();
    }

    @Override
    public List<PricingRule> getAllRules() {
        return new ArrayList<>();
    }
}
