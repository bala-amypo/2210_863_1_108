package com.example.demo.service;

import com.example.demo.model.PriceAdjustmentLog;

import java.util.List;

public interface PriceAdjustmentLogService {
    
    PriceAdjustmentLog createLog(PriceAdjustmentLog log);
    
    List<PriceAdjustmentLog> getAdjustmentsByEvent(Long eventId);
    
    List<PriceAdjustmentLog> getAllAdjustments();
}