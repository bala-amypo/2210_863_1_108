package com.example.demo.controller;

import com.example.demo.model.EventRecord;
import com.example.demo.service.EventRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Event Management")
public class EventRecordController {
    
    private final EventRecordService eventRecordService;
    
    public EventRecordController(EventRecordService eventRecordService) {
        this.eventRecordService = eventRecordService;
    }
    
    @PostMapping
    public ResponseEntity<EventRecord> createEvent(@RequestBody EventRecord event) {
        EventRecord created = eventRecordService.createEvent(event);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventRecord> getEvent(@PathVariable Long id) {
        EventRecord event = eventRecordService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping
    public ResponseEntity<List<EventRecord>> getAllEvents() {
        List<EventRecord> events = eventRecordService.getAllEvents();
        return ResponseEntity.ok(events);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<EventRecord> updateStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, Boolean> statusUpdate) {
        boolean active = statusUpdate.get("active");
        EventRecord updated = eventRecordService.updateEventStatus(id, active);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/lookup/{eventCode}")
    public ResponseEntity<EventRecord> lookupByCode(@PathVariable String eventCode) {
        Optional<EventRecord> event = eventRecordService.getEventByCode(eventCode);
        return event.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}