package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pricing_rules")
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ruleCode;

    private Integer minRemainingSeats;

    private Integer maxRemainingSeats;

    private Integer daysBeforeEvent;

    @Column(nullable = false)
    private Double priceMultiplier;

    @Column(nullable = false)
    private Boolean active = true;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Integer getMinRemainingSeats() {
        return minRemainingSeats;
    }

    public void setMinRemainingSeats(Integer minRemainingSeats) {
        this.minRemainingSeats = minRemainingSeats;
    }

    public Integer getMaxRemainingSeats() {
        return maxRemainingSeats;
    }

    public void setMaxRemainingSeats(Integer maxRemainingSeats) {
        this.maxRemainingSeats = maxRemainingSeats;
    }

    public Integer getDaysBeforeEvent() {
        return daysBeforeEvent;
    }

    public void setDaysBeforeEvent(Integer daysBeforeEvent) {
        this.daysBeforeEvent = daysBeforeEvent;
    }

    public Double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(Double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}