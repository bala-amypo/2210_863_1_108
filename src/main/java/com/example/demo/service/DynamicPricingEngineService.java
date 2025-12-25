package com.example.demo.service;

import com.example.demo.model.DynamicPriceRecord;

import java.util.List;

public interface DynamicPricingEngineService {
    
    DynamicPriceRecord computeDynamicPrice(Long eventId);
    
    List<DynamicPriceRecord> getPriceHistory(Long eventId);
    
    List<DynamicPriceRecord> getAllComputedPrices();
}