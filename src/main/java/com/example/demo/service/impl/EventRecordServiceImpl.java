package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.EventRecord;
import com.example.demo.repository.EventRecordRepository;
import com.example.demo.service.EventRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventRecordServiceImpl implements EventRecordService {

    private final EventRecordRepository eventRecordRepository;

    @Autowired
    public EventRecordServiceImpl(EventRecordRepository eventRecordRepository) {
        this.eventRecordRepository = eventRecordRepository;
    }

    @Override
    public EventRecord createEvent(EventRecord event) {
        if (event.getBasePrice() == null || event.getBasePrice() <= 0) {
            throw new BadRequestException("Base price must be > 0");
        }
        
        if (eventRecordRepository.existsByEventCode(event.getEventCode())) {
            throw new BadRequestException("Event code already exists: " + event.getEventCode());
        }
        
        return eventRecordRepository.save(event);
    }

    @Override
    public EventRecord getEventById(Long id) {
        return eventRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
    }

    @Override
    public Optional<EventRecord> getEventByCode(String eventCode) {
        return eventRecordRepository.findByEventCode(eventCode);
    }

    @Override
    public List<EventRecord> getAllEvents() {
        return eventRecordRepository.findAll();
    }

    @Override
    public EventRecord updateEventStatus(Long id, Boolean active) {
        EventRecord event = getEventById(id);
        event.setActive(active);
        return eventRecordRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRecordRepository.existsById(id)) {
            throw new NotFoundException("Event not found with id: " + id);
        }
        eventRecordRepository.deleteById(id);
    }
}