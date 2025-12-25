package com.example.demo.controller;

import com.example.demo.model.SeatInventoryRecord;
import com.example.demo.service.SeatInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Seat Inventory", description = "Seat inventory management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class SeatInventoryController {

    @Autowired
    private SeatInventoryService seatInventoryService;

    @PostMapping
    @Operation(summary = "Create seat inventory for an event")
    public ResponseEntity<SeatInventoryRecord> createInventory(@RequestBody SeatInventoryRecord inventory) {
        SeatInventoryRecord created = seatInventoryService.createInventory(inventory);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get inventory by event ID")
    public ResponseEntity<SeatInventoryRecord> getInventoryByEvent(@PathVariable Long eventId) {
        SeatInventoryRecord inventory = seatInventoryService.getInventoryByEvent(eventId);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/event/{eventId}")
    @Operation(summary = "Update remaining seats for an event")
    public ResponseEntity<SeatInventoryRecord> updateInventory(
            @PathVariable Long eventId,
            @RequestParam Integer remainingSeats) {
        SeatInventoryRecord updated = seatInventoryService.updateInventory(eventId, remainingSeats);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @Operation(summary = "Get all seat inventories")
    public ResponseEntity<List<SeatInventoryRecord>> getAllInventories() {
        List<SeatInventoryRecord> inventories = seatInventoryService.getAllInventories();
        return ResponseEntity.ok(inventories);
    }
}