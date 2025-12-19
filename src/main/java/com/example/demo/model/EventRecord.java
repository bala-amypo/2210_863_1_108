package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_record")
public class EventRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String eventCode;

    @Column(nullable = false)
    private String eventName;
    
    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active;
}

public EventRecord(){

}
public EventRecord(String eventCode,String eventName,String venue,LocalDate eventDate,Double basePrice,Boolean active) {
    this.eventCode = eventCode;
    this.eventName = eventName;
    this.venue = venue;
    this.eventDate = eventDate;
    this.basePrice = basePrice;
    this.active = active;
}

@PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public Long getId(){
         return id;
    }

    public void setId(Long id){
        this.id = id;
    }

     public Long getEventId(){
        return eventId;
    }

