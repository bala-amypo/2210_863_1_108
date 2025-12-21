package com.example.demo.service;

import com.example.demo.model.DynamicPriceRecord;
import java.util.List;
import java.util.Optional;

public interface DynamicPricingEngineService {
    
    DynamicPriceRecord computeDynamicPrice(Long eventId);
    
    List<DynamicPriceRecord> getPriceHistory(Long eventId);
    
    Optional<DynamicPriceRecord> getLatestPrice(Long eventId);
    
    List<DynamicPriceRecord> getAllComputedPrices();
}