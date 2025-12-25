package com.example.demo.service.impl;

import com.example.demo.model.PriceAdjustmentLog;
import com.example.demo.repository.PriceAdjustmentLogRepository;
import com.example.demo.service.PriceAdjustmentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceAdjustmentLogServiceImpl implements PriceAdjustmentLogService {

    private final PriceAdjustmentLogRepository priceAdjustmentLogRepository;

    @Autowired
    public PriceAdjustmentLogServiceImpl(PriceAdjustmentLogRepository priceAdjustmentLogRepository) {
        this.priceAdjustmentLogRepository = priceAdjustmentLogRepository;
    }

    @Override
    public PriceAdjustmentLog createLog(PriceAdjustmentLog log) {
        return priceAdjustmentLogRepository.save(log);
    }

    @Override
    public List<PriceAdjustmentLog> getAdjustmentsByEvent(Long eventId) {
        return priceAdjustmentLogRepository.findByEventId(eventId);
    }

    @Override
    public List<PriceAdjustmentLog> getAllAdjustments() {
        return priceAdjustmentLogRepository.findAll();
    }
}