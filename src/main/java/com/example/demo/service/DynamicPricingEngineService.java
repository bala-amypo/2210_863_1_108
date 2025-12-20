package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.*;
import com.example.demo.repository.DynamicPriceRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DynamicPricingEngineService {
    
    private final EventRecordService eventRecordService;
    private final SeatInventoryService seatInventoryService;
    private final PricingRuleService pricingRuleService;
    private final DynamicPriceRecordRepository dynamicPriceRecordRepository;
    private final PriceAdjustmentLogService priceAdjustmentLogService;
    
    public DynamicPricingEngineService(
            EventRecordService eventRecordService,
            SeatInventoryService seatInventoryService,
            PricingRuleService pricingRuleService,
            DynamicPriceRecordRepository dynamicPriceRecordRepository,
            PriceAdjustmentLogService priceAdjustmentLogService) {
        this.eventRecordService = eventRecordService;
        this.seatInventoryService = seatInventoryService;
        this.pricingRuleService = pricingRuleService;
        this.dynamicPriceRecordRepository = dynamicPriceRecordRepository;
        this.priceAdjustmentLogService = priceAdjustmentLogService;
    }
    
    @Transactional
    public DynamicPriceRecord computeDynamicPrice(Long eventId) {
        // 1. Fetch event (throw if inactive)
        EventRecord event = eventRecordService.getEventById(eventId);
        if (!event.getActive()) {
            throw new BadRequestException("Event is not active");
        }
        
        // 2. Fetch inventory (throw if missing)
        SeatInventoryRecord inventory = seatInventoryService.getInventoryByEvent(eventId);
        
        // 3. Calculate days before event
        long daysBeforeEvent = ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());
        
        // 4. Get active rules
        List<PricingRule> activeRules = pricingRuleService.getActiveRules();
        
        // 5. Find matching rules
        List<PricingRule> matchingRules = new ArrayList<>();
        for (PricingRule rule : activeRules) {
            boolean seatsMatch = inventory.getRemainingSeats() >= rule.getMinRemainingSeats() &&
                               inventory.getRemainingSeats() <= rule.getMaxRemainingSeats();
            boolean daysMatch = daysBeforeEvent <= rule.getDaysBeforeEvent();
            
            if (seatsMatch && daysMatch) {
                matchingRules.add(rule);
            }
        }
        
        // 6. Compute price (apply all matching multipliers)
        double computedPrice = event.getBasePrice();
        for (PricingRule rule : matchingRules) {
            computedPrice *= rule.getPriceMultiplier();
        }
        
        // 7. Validate computed price
        if (computedPrice <= 0) {
            throw new BadRequestException("Computed price must be > 0");
        }
        
        // 8. Get previous price (if any)
        Optional<DynamicPriceRecord> previousRecord = 
            dynamicPriceRecordRepository.findFirstByEventIdOrderByComputedAtDesc(eventId);
        
        // 9. Build applied rule codes string
        StringBuilder appliedRuleCodes = new StringBuilder();
        for (int i = 0; i < matchingRules.size(); i++) {
            appliedRuleCodes.append(matchingRules.get(i).getRuleCode());
            if (i < matchingRules.size() - 1) {
                appliedRuleCodes.append(",");
            }
        }
        
        // 10. Save dynamic price record
        DynamicPriceRecord priceRecord = new DynamicPriceRecord(
            eventId, 
            computedPrice, 
            appliedRuleCodes.toString()
        );
        priceRecord = dynamicPriceRecordRepository.save(priceRecord);
        
        // 11. Log adjustment if price changed materially
        if (previousRecord.isPresent()) {
            double oldPrice = previousRecord.get().getComputedPrice();
            double priceDifference = Math.abs(computedPrice - oldPrice);
            double threshold = oldPrice * 0.01; // 1% threshold
            
            if (priceDifference > threshold) {
                String reason = "Price changed from " + oldPrice + " to " + computedPrice + 
                              " due to rules: " + appliedRuleCodes.toString();
                priceAdjustmentLogService.logAdjustment(
                    new PriceAdjustmentLog(eventId, oldPrice, computedPrice, reason)
                );
            }
        }
        
        return priceRecord;
    }
    
    public List<DynamicPriceRecord> getPriceHistory(Long eventId) {
        return dynamicPriceRecordRepository.findByEventIdOrderByComputedAtDesc(eventId);
    }
    
    public DynamicPriceRecord getLatestPrice(Long eventId) {
        return dynamicPriceRecordRepository.findFirstByEventIdOrderByComputedAtDesc(eventId)
                .orElse(null);
    }
    
    public List<DynamicPriceRecord> getAllComputedPrices() {
        return dynamicPriceRecordRepository.findAll();
    }
}