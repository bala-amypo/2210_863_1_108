package com.example.demo.service;

import com.example.demo.model.EventRecord;

import java.util.List;
import java.util.Optional;

public interface EventRecordService {
    
    EventRecord createEvent(EventRecord event);
    
    EventRecord getEventById(Long id);
    
    Optional<EventRecord> getEventByCode(String eventCode);
    
    List<EventRecord> getAllEvents();
    
    EventRecord updateEventStatus(Long id, Boolean active);
    
    void deleteEvent(Long id);
}