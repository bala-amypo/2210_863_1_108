package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.EventRecord;
import com.example.demo.model.SeatInventoryRecord;
import com.example.demo.repository.SeatInventoryRecordRepository;
import com.example.demo.repository.EventRecordRepository;
import com.example.demo.service.SeatInventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SeatInventoryServiceImpl implements SeatInventoryService {
    
    private final SeatInventoryRecordRepository inventoryRepository;
    private final EventRecordRepository eventRepository;
    
    public SeatInventoryServiceImpl(SeatInventoryRecordRepository inventoryRepository,
                                    EventRecordRepository eventRepository) {
        this.inventoryRepository = inventoryRepository;
        this.eventRepository = eventRepository;
    }
    
    @Override
    @Transactional
    public SeatInventoryRecord createInventory(SeatInventoryRecord inventory) {
        // Validate: Event must exist
        EventRecord event = eventRepository.findById(inventory.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found"));
        
        // Validate: Remaining seats cannot exceed total seats
        if (inventory.getRemainingSeats() > inventory.getTotalSeats()) {
            throw new BadRequestException("Remaining seats cannot exceed total seats");
        }
        
        return inventoryRepository.save(inventory);
    }
    
    @Override
    @Transactional
    public SeatInventoryRecord updateRemainingSeats(Long eventId, Integer remainingSeats) {
        SeatInventoryRecord inventory = getInventoryByEvent(eventId)
                .orElseThrow(() -> new NotFoundException("Seat inventory not found"));
        
        // Validate: Remaining seats cannot exceed total seats
        if (remainingSeats > inventory.getTotalSeats()) {
            throw new BadRequestException("Remaining seats cannot exceed total seats");
        }
        
        inventory.setRemainingSeats(remainingSeats);
        return inventoryRepository.save(inventory);
    }
    
    @Override
    public Optional<SeatInventoryRecord> getInventoryByEvent(Long eventId) {
        return inventoryRepository.findByEventId(eventId);
    }
    
    @Override
    public List<SeatInventoryRecord> getAllInventories() {
        return inventoryRepository.findAll();
    }
}