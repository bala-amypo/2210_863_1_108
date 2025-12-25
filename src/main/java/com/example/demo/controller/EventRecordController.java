package com.example.demo.controller;

import com.example.demo.model.EventRecord;
import com.example.demo.service.EventRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class EventRecordController {

    @Autowired
    private EventRecordService eventRecordService;

    @PostMapping
    @Operation(summary = "Create a new event")
    public ResponseEntity<EventRecord> createEvent(@RequestBody EventRecord event) {
        EventRecord created = eventRecordService.createEvent(event);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<EventRecord> getEventById(@PathVariable Long id) {
        EventRecord event = eventRecordService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    @Operation(summary = "Get all events")
    public ResponseEntity<List<EventRecord>> getAllEvents() {
        List<EventRecord> events = eventRecordService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/code/{eventCode}")
    @Operation(summary = "Get event by code")
    public ResponseEntity<EventRecord> getEventByCode(@PathVariable String eventCode) {
        return eventRecordService.getEventByCode(eventCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update event status")
    public ResponseEntity<EventRecord> updateEventStatus(@PathVariable Long id, @RequestParam Boolean active) {
        EventRecord updated = eventRecordService.updateEventStatus(id, active);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventRecordService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}