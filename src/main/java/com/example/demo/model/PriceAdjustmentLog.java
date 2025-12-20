package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_adjustment_log")
public class PriceAdjustmentLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long eventId;
    
    @Column(nullable = false)
    private Double oldPrice;
    
    @Column(nullable = false)
    private Double newPrice;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(nullable = false)
    private LocalDateTime changedAt;
    
    // Constructors
    public PriceAdjustmentLog() {
    }
    
    public PriceAdjustmentLog(Long eventId, Double oldPrice, Double newPrice, String reason) {
        this.eventId = eventId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.reason = reason;
    }
    
    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public Double getOldPrice() {
        return oldPrice;
    }
    
    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }
    
    public Double getNewPrice() {
        return newPrice;
    }
    
    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getChangedAt() {
        return changedAt;
    }
    
    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
