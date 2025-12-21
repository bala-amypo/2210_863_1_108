package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.EventRecord;
import com.example.demo.repository.EventRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EventRecordServiceImpl {
    
    private final EventRecordRepository eventRecordRepository;
    
    public EventRecordServiceImpl(EventRecordRepository eventRecordRepository) {
        this.eventRecordRepository = eventRecordRepository;
    }
    
    @Transactional
    public EventRecord createEvent(EventRecord event) {
        // Validate: Check for duplicate event code
        if (eventRecordRepository.existsByEventCode(event.getEventCode())) {
            throw new BadRequestException("Event code already exists");
        }
        
        // Validate: Base price must be > 0
        if (event.getBasePrice() == null || event.getBasePrice() <= 0) {
            throw new BadRequestException("Base price must be > 0");
        }
        
        return eventRecordRepository.save(event);
    }
    
    public EventRecord getEventById(Long id) {
        return eventRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }
    
    public EventRecord getEventByCode(String eventCode) {
        return eventRecordRepository.findByEventCode(eventCode)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }
    
    public List<EventRecord> getAllEvents() {
        return eventRecordRepository.findAll();
    }
    
    @Transactional
    public void updateEventStatus(Long id, boolean active) {
        EventRecord event = getEventById(id);
        event.setActive(active);
        eventRecordRepository.save(event);
    }
}