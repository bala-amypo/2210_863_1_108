package com.example.demo.service;

import com.example.demo.model.SeatInventoryRecord;

import java.util.List;

public interface SeatInventoryService {
    
    SeatInventoryRecord createInventory(SeatInventoryRecord inventory);
    
    SeatInventoryRecord getInventoryByEvent(Long eventId);
    
    SeatInventoryRecord updateInventory(Long eventId, Integer remainingSeats);
    
    List<SeatInventoryRecord> getAllInventories();
}