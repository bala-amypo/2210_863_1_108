package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.SeatInventoryRecord;
import com.example.demo.repository.EventRecordRepository;
import com.example.demo.repository.SeatInventoryRecordRepository;
import com.example.demo.service.SeatInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatInventoryServiceImpl implements SeatInventoryService {

    private final SeatInventoryRecordRepository seatInventoryRecordRepository;
    private final EventRecordRepository eventRecordRepository;

    @Autowired
    public SeatInventoryServiceImpl(SeatInventoryRecordRepository seatInventoryRecordRepository,
                                     EventRecordRepository eventRecordRepository) {
        this.seatInventoryRecordRepository = seatInventoryRecordRepository;
        this.eventRecordRepository = eventRecordRepository;
    }

    @Override
public SeatInventoryRecord createInventory(SeatInventoryRecord inventory) {
    // Validate remaining seats
    if (inventory.getRemainingSeats() > inventory.getTotalSeats()) {
        throw new BadRequestException("Remaining seats cannot exceed total seats");
    }
    
    // Validate event exists (optional - only if event repository has the event)
    if (inventory.getEventId() != null && eventRecordRepository.existsById(inventory.getEventId())) {
        // Event exists, proceed
    }
    
    return seatInventoryRecordRepository.save(inventory);
}

    @Override
    public SeatInventoryRecord getInventoryByEvent(Long eventId) {
        return seatInventoryRecordRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Seat inventory not found for event: " + eventId));
    }

    @Override
    public SeatInventoryRecord updateInventory(Long eventId, Integer remainingSeats) {
        SeatInventoryRecord inventory = getInventoryByEvent(eventId);
        
        if (remainingSeats > inventory.getTotalSeats()) {
            throw new BadRequestException("Remaining seats cannot exceed total seats");
        }
        
        inventory.setRemainingSeats(remainingSeats);
        return seatInventoryRecordRepository.save(inventory);
    }

    @Override
    public List<SeatInventoryRecord> getAllInventories() {
        return seatInventoryRecordRepository.findAll();
    }
}