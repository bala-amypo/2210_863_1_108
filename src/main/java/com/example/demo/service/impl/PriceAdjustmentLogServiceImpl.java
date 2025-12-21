package com.example.demo.service.impl;

import com.example.demo.model.PriceAdjustmentLog;
import com.example.demo.repository.PriceAdjustmentLogRepository;
import com.example.demo.service.PriceAdjustmentLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PriceAdjustmentLogServiceImpl implements PriceAdjustmentLogService {
    
    private final PriceAdjustmentLogRepository logRepository;
    
    public PriceAdjustmentLogServiceImpl(PriceAdjustmentLogRepository logRepository) {
        this.logRepository = logRepository;
    }
    
    @Override
    @Transactional
    public PriceAdjustmentLog logAdjustment(PriceAdjustmentLog log) {
        return logRepository.save(log);
    }
    
    @Override
    public List<PriceAdjustmentLog> getAdjustmentsByEvent(Long eventId) {
        return logRepository.findByEventId(eventId);
    }
    
    @Override
    public List<PriceAdjustmentLog> getAllAdjustments() {
        return logRepository.findAll();
    }
}