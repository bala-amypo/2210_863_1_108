package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.DynamicPriceRecord;
import com.example.demo.model.EventRecord;
import com.example.demo.model.PricingRule;
import com.example.demo.model.PriceAdjustmentLog;
import com.example.demo.model.SeatInventoryRecord;
import com.example.demo.repository.DynamicPriceRecordRepository;
import com.example.demo.repository.EventRecordRepository;
import com.example.demo.repository.SeatInventoryRecordRepository;
import com.example.demo.repository.PricingRuleRepository;
import com.example.demo.repository.PriceAdjustmentLogRepository;
import com.example.demo.service.DynamicPricingEngineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DynamicPricingEngineServiceImpl
        implements DynamicPricingEngineService {

    private final EventRecordRepository eventRecordRepository;
    private final SeatInventoryRecordRepository seatInventoryRecordRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final DynamicPriceRecordRepository dynamicPriceRecordRepository;
    private final PriceAdjustmentLogRepository priceAdjustmentLogRepository;

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

    // =====================================================
    // Compute Dynamic Price
    // =====================================================
    @Override
    @Transactional
    public DynamicPriceRecord computeDynamicPrice(Long eventId) {

        // 1. Fetch event
        EventRecord event = eventRecordRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException("Event not found"));

        if (!event.getActive()) {
            throw new BadRequestException("Event is not active");
        }

        // 2. Fetch seat inventory
        SeatInventoryRecord inventory = seatInventoryRecordRepository
                .findByEventId(eventId)
                .orElseThrow(() -> new BadRequestException("Seat inventory not found"));

        // 3. Calculate days before event
        long daysBeforeEvent =
                ChronoUnit.DAYS.between(LocalDate.now(), event.getEventDate());

        // 4. Fetch active pricing rules
        List<PricingRule> activeRules = pricingRuleRepository.findByActiveTrue();

        // 5. Match rules
        List<PricingRule> matchingRules = new ArrayList<>();
        for (PricingRule rule : activeRules) {

            boolean seatsMatch =
                    inventory.getRemainingSeats() >= rule.getMinRemainingSeats()
                    && inventory.getRemainingSeats() <= rule.getMaxRemainingSeats();

            boolean daysMatch =
                    daysBeforeEvent <= rule.getDaysBeforeEvent();

            if (seatsMatch && daysMatch) {
                matchingRules.add(rule);
            }
        }

        // 6. Compute price
        double computedPrice = event.getBasePrice();
        for (PricingRule rule : matchingRules) {
            computedPrice *= rule.getPriceMultiplier();
        }

        if (computedPrice <= 0) {
            throw new BadRequestException("Computed price must be > 0");
        }

        // 7. Get previous price
        Optional<DynamicPriceRecord> previousRecord =
                dynamicPriceRecordRepository
                        .findFirstByEventIdOrderByComputedAtDesc(eventId);

        // 8. Build applied rule codes
        StringBuilder appliedRuleCodes = new StringBuilder();
        for (int i = 0; i < matchingRules.size(); i++) {
            appliedRuleCodes.append(matchingRules.get(i).getRuleCode());
            if (i < matchingRules.size() - 1) {
                appliedRuleCodes.append(",");
            }
        }

        // 9. Save new price
        DynamicPriceRecord priceRecord = new DynamicPriceRecord(
                eventId,
                computedPrice,
                appliedRuleCodes.toString()
        );

        priceRecord = dynamicPriceRecordRepository.save(priceRecord);

        // 10. Log adjustment if significant change
        if (previousRecord.isPresent()) {

            double oldPrice = previousRecord.get().getComputedPrice();
            double difference = Math.abs(computedPrice - oldPrice);
            double threshold = oldPrice * 0.01; // 1%

            if (difference > threshold) {
                String reason =
                        "Price changed from " + oldPrice +
                        " to " + computedPrice +
                        " due to rules: " + appliedRuleCodes;

                PriceAdjustmentLog log =
                        new PriceAdjustmentLog(eventId, oldPrice, computedPrice, reason);

                priceAdjustmentLogRepository.save(log);
            }
        }

        return priceRecord;
    }

    // =====================================================
    // History & Queries
    // =====================================================
    @Override
    public List<DynamicPriceRecord> getPriceHistory(Long eventId) {
        return dynamicPriceRecordRepository
                .findByEventIdOrderByComputedAtDesc(eventId);
    }

    @Override
    public Optional<DynamicPriceRecord> getLatestPrice(Long eventId) {
        return dynamicPriceRecordRepository
                .findFirstByEventIdOrderByComputedAtDesc(eventId);
    }

    @Override
    public List<DynamicPriceRecord> getAllComputedPrices() {
        return dynamicPriceRecordRepository.findAll();
    }
}
