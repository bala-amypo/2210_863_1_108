package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.DynamicPricingEngineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DynamicPricingEngineServiceImpl implements DynamicPricingEngineService {
    
    private final EventRecordRepository eventRepository;
    private final SeatInventoryRecordRepository inventoryRepository;
    private final PricingRuleRepository ruleRepository;
    private final DynamicPriceRecordRepository priceRepository;
    private final PriceAdjustmentLogRepository logRepository;
    
    public DynamicPricingEngineServiceImpl(
            EventRecordRepository eventRepository,
            SeatInventoryRecordRepository inventoryRepository,
            PricingRuleRepository ruleRepository,
            DynamicPriceRecordRepository priceRepository,
            PriceAdjustmentLogRepository logRepository) {
        this.eventRepository = eventRepository;
        this.inventoryRepository = inventoryRepository;
        this.ruleRepository = ruleRepository;
        this.priceRepository = priceRepository;
        this.logRepository = logRepository;
    }
    
    @Override
    @Transactional
    public DynamicPriceRecord computeDynamicPrice(Long eventId) {
        // 1. Fetch event (throw if not found)
        EventRecord event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException("Event not found"));
        
        // 2. Validate event is active
        if (!event.getActive()) {
            throw new BadRequestException("Event is not active");
        }
        
        // 3. Fetch inventory (throw if missing)
        SeatInventoryRecord inventory = inventoryRepository.findByEventId(eventId)
                .orElseThrow(() -> new BadRequestException("Seat inventory not found"));
        
        // 4. Calculate days before event
        long daysBeforeEvent = ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());
        
        // 5. Get active rules
        List<PricingRule> activeRules = ruleRepository.findByActiveTrue();
        
        // 6. Find matching rules
        List<PricingRule> matchingRules = new ArrayList<>();
        for (PricingRule rule : activeRules) {
            boolean seatsMatch = inventory.getRemainingSeats() >= rule.getMinRemainingSeats() &&
                               inventory.getRemainingSeats() <= rule.getMaxRemainingSeats();
            boolean daysMatch = daysBeforeEvent <= rule.getDaysBeforeEvent();
            
            if (seatsMatch && daysMatch) {
                matchingRules.add(rule);
            }
        }
        
        // 7. Compute price (apply all matching multipliers)
        double computedPrice = event.getBasePrice();
        for (PricingRule rule : matchingRules) {
            computedPrice *= rule.getPriceMultiplier();
        }
        
        // 8. Validate computed price
        if (computedPrice <= 0) {
            throw new BadRequestException("Computed price must be > 0");
        }
        
        // 9. Get previous price (if any)
        Optional<DynamicPriceRecord> previousRecord = 
            priceRepository.findFirstByEventIdOrderByComputedAtDesc(eventId);
        
        // 10. Build applied rule codes string
        StringBuilder appliedRuleCodes = new StringBuilder();
        for (int i = 0; i < matchingRules.size(); i++) {
            appliedRuleCodes.append(matchingRules.get(i).getRuleCode());
            if (i < matchingRules.size() - 1) {
                appliedRuleCodes.append(",");
            }
        }
        
        // 11. Save dynamic price record
        DynamicPriceRecord priceRecord = new DynamicPriceRecord(
            eventId, 
            computedPrice, 
            appliedRuleCodes.toString()
        );
        priceRecord = priceRepository.save(priceRecord);
        
        // 12. Log adjustment if price changed materially
        if (previousRecord.isPresent()) {
            double oldPrice = previousRecord.get().getComputedPrice();
            double priceDifference = Math.abs(computedPrice - oldPrice);
            double threshold = oldPrice * 0.01; // 1% threshold
            
            if (priceDifference > threshold) {
                String reason = "Price changed from " + oldPrice + " to " + computedPrice + 
                              " due to rules: " + appliedRuleCodes.toString();
                PriceAdjustmentLog log = new PriceAdjustmentLog(eventId, oldPrice, computedPrice, reason);
                logRepository.save(log);
            }
        }
        
        return priceRecord;
    }
    
    @Override
    public List<DynamicPriceRecord> getPriceHistory(Long eventId) {
        return priceRepository.findByEventIdOrderByComputedAtDesc(eventId);
    }
    
    @Override
    public Optional<DynamicPriceRecord> getLatestPrice(Long eventId) {
        return priceRepository.findFirstByEventIdOrderByComputedAtDesc(eventId);
    }
    
    @Override
    public List<DynamicPriceRecord> getAllComputedPrices() {
        return priceRepository.findAll();
    }
}