package com.example.demo.service;

import com.example.demo.model.SeatInventoryRecord;
import java.util.List;
import java.util.Optional;

public interface SeatInventoryService {
    
    SeatInventoryRecord createInventory(SeatInventoryRecord inventory);
    
    SeatInventoryRecord updateRemainingSeats(Long eventId, Integer remainingSeats);
    
    Optional<SeatInventoryRecord> getInventoryByEvent(Long eventId);
    
    List<SeatInventoryRecord> getAllInventories();
}