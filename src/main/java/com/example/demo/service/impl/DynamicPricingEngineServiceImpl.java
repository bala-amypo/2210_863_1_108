package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.DynamicPricingEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DynamicPricingEngineServiceImpl implements DynamicPricingEngineService {

    private final EventRecordRepository eventRecordRepository;
    private final SeatInventoryRecordRepository seatInventoryRecordRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final DynamicPriceRecordRepository dynamicPriceRecordRepository;
    private final PriceAdjustmentLogRepository priceAdjustmentLogRepository;

    @Autowired
    public DynamicPricingEngineServiceImpl(
            EventRecordRepository eventRecordRepository,
            SeatInventoryRecordRepository seatInventoryRecordRepository,
            PricingRuleRepository pricingRuleRepository,
            DynamicPriceRecordRepository dynamicPriceRecordRepository,
            PriceAdjustmentLogRepository priceAdjustmentLogRepository) {
        this.eventRecordRepository = eventRecordRepository;
        this.seatInventoryRecordRepository = seatInventoryRecordRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.dynamicPriceRecordRepository = dynamicPriceRecordRepository;
        this.priceAdjustmentLogRepository = priceAdjustmentLogRepository;
    }

    @Override
    public DynamicPriceRecord computeDynamicPrice(Long eventId) {
        // Get event
        EventRecord event = eventRecordRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        // Check if event is active
        if (!event.getActive()) {
            throw new BadRequestException("Event is not active");
        }

        // Get seat inventory
        SeatInventoryRecord inventory = seatInventoryRecordRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Seat inventory not found for event: " + eventId));

        // Calculate days before event
        long daysBeforeEvent = ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());

        // Get active pricing rules
        List<PricingRule> activeRules = pricingRuleRepository.findByActiveTrue();

        // Find applicable rules
        List<PricingRule> applicableRules = new ArrayList<>();
        for (PricingRule rule : activeRules) {
            boolean seatsMatch = (rule.getMinRemainingSeats() == null || inventory.getRemainingSeats() >= rule.getMinRemainingSeats()) &&
                                (rule.getMaxRemainingSeats() == null || inventory.getRemainingSeats() <= rule.getMaxRemainingSeats());
            
            boolean daysMatch = rule.getDaysBeforeEvent() == null || daysBeforeEvent <= rule.getDaysBeforeEvent();
            
            if (seatsMatch && daysMatch) {
                applicableRules.add(rule);
            }
        }

        // Calculate price
        double computedPrice = event.getBasePrice();
        StringBuilder appliedRuleCodes = new StringBuilder();
        
        for (PricingRule rule : applicableRules) {
            computedPrice *= rule.getPriceMultiplier();
            if (appliedRuleCodes.length() > 0) {
                appliedRuleCodes.append(",");
            }
            appliedRuleCodes.append(rule.getRuleCode());
        }

        // Get previous price
        Optional<DynamicPriceRecord> previousPriceOpt = 
                dynamicPriceRecordRepository.findFirstByEventIdOrderByComputedAtDesc(eventId);

        // Create new price record
        DynamicPriceRecord priceRecord = new DynamicPriceRecord();
        priceRecord.setEventId(eventId);
        priceRecord.setComputedPrice(computedPrice);
        priceRecord.setAppliedRuleCodes(appliedRuleCodes.toString());
        priceRecord = dynamicPriceRecordRepository.save(priceRecord);

        // Log price adjustment if price changed
        if (previousPriceOpt.isPresent()) {
            DynamicPriceRecord previousPrice = previousPriceOpt.get();
            if (!previousPrice.getComputedPrice().equals(computedPrice)) {
                PriceAdjustmentLog log = new PriceAdjustmentLog();
                log.setEventId(eventId);
                log.setOldPrice(previousPrice.getComputedPrice());
                log.setNewPrice(computedPrice);
                log.setReason("Dynamic pricing adjustment based on rules: " + appliedRuleCodes.toString());
                priceAdjustmentLogRepository.save(log);
            }
        }

        return priceRecord;
    }

    @Override
    public List<DynamicPriceRecord> getPriceHistory(Long eventId) {
        return dynamicPriceRecordRepository.findByEventIdOrderByComputedAtDesc(eventId);
    }

    @Override
    public List<DynamicPriceRecord> getAllComputedPrices() {
        return dynamicPriceRecordRepository.findAll();
    }
}