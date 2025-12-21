package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.EventRecord;
import com.example.demo.model.SeatInventoryRecord;
import com.example.demo.repository.SeatInventoryRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SeatInventoryService {
    
    private final SeatInventoryRecordRepository seatInventoryRecordRepository;
    private final EventRecordService eventRecordService;
    
    public SeatInventoryService(SeatInventoryRecordRepository seatInventoryRecordRepository,
                               EventRecordService eventRecordService) {
        this.seatInventoryRecordRepository = seatInventoryRecordRepository;
        this.eventRecordService = eventRecordService;
    }
    
    @Transactional
    public SeatInventoryRecord createInventory(SeatInventoryRecord inventory) {
        // Validate: Event must exist
        EventRecord event = eventRecordService.getEventById(inventory.getEventId());
        
        // Validate: Remaining seats cannot exceed total seats
        if (inventory.getRemainingSeats() > inventory.getTotalSeats()) {
            throw new BadRequestException("Remaining seats cannot exceed total seats");
        }
        
        return seatInventoryRecordRepository.save(inventory);
    }
    
    @Transactional
public SeatInventoryRecord updateRemainingSeats(Long eventId, Integer remainingSeats) {
    SeatInventoryRecord inventory = getInventoryByEvent(eventId);

    if (remainingSeats > inventory.getTotalSeats()) {
        throw new BadRequestException("Remaining seats cannot exceed total seats");
    }

    inventory.setRemainingSeats(remainingSeats);
    return seatInventoryRecordRepository.save(inventory);
}

    
    public SeatInventoryRecord getInventoryByEvent(Long eventId) {
        return seatInventoryRecordRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Seat inventory not found"));
    }
    
    public List<SeatInventoryRecord> getAllInventories() {
        return seatInventoryRecordRepository.findAll();
    }
}