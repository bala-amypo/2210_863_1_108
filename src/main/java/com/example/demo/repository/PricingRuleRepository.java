package com.example.demo.repository;

import com.example.demo.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    
    boolean existsByRuleCode(String code);
    
    List<PricingRule> findByActiveTrue();
    
    Optional<PricingRule> findByRuleCode(String code);
}