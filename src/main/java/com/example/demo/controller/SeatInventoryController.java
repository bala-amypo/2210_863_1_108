package com.example.demo.controller;

import com.example.demo.model.SeatInventoryRecord;
import com.example.demo.service.SeatInventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Seat Inventory Management")
public class SeatInventoryController {
    
    private final SeatInventoryService seatInventoryService;
    
    public SeatInventoryController(SeatInventoryService seatInventoryService) {
        this.seatInventoryService = seatInventoryService;
    }
    
    @PostMapping
    public ResponseEntity<SeatInventoryRecord> createInventory(@RequestBody SeatInventoryRecord inventory) {
        SeatInventoryRecord created = seatInventoryService.createInventory(inventory);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{eventId}/remaining")
    public ResponseEntity<SeatInventoryRecord> updateRemainingSeats(
            @PathVariable Long eventId,
            @RequestBody Map<String, Integer> update) {
        Integer remainingSeats = update.get("remainingSeats");
        SeatInventoryRecord updated = seatInventoryService.updateRemainingSeats(eventId, remainingSeats);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<SeatInventoryRecord> getByEvent(@PathVariable Long eventId) {
        Optional<SeatInventoryRecord> inventory = seatInventoryService.getInventoryByEvent(eventId);
        return inventory.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<SeatInventoryRecord>> getAllInventories() {
        List<SeatInventoryRecord> inventories = seatInventoryService.getAllInventories();
        return ResponseEntity.ok(inventories);
    }
}