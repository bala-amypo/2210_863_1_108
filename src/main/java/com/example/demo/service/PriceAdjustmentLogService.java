package com.example.demo.service;

import com.example.demo.model.PriceAdjustmentLog;
import com.example.demo.repository.PriceAdjustmentLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PriceAdjustmentLogService {
    
    private final PriceAdjustmentLogRepository priceAdjustmentLogRepository;
    
    public PriceAdjustmentLogService(PriceAdjustmentLogRepository priceAdjustmentLogRepository) {
        this.priceAdjustmentLogRepository = priceAdjustmentLogRepository;
    }
    
    @Transactional
    public PriceAdjustmentLog logAdjustment(PriceAdjustmentLog log) {
        return priceAdjustmentLogRepository.save(log);
    }
    
    public List<PriceAdjustmentLog> getAdjustmentsByEvent(Long eventId) {
        return priceAdjustmentLogRepository.findByEventId(eventId);
    }
    
    public List<PriceAdjustmentLog> getAllAdjustments() {
        return priceAdjustmentLogRepository.findAll();
    }
    
    public PriceAdjustmentLog getAdjustmentById(Long id) {
        return priceAdjustmentLogRepository.findById(id).orElse(null);
    }
}