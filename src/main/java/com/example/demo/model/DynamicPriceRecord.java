package com.example.demo.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="dynamic_price_record")
public class DynamicPriceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

     @Column(nullable = false)
    private Double computedPrice;

     @Column(nullable = false)
    private String appliedRuleCodes;

     @Column(nullable = false)
    private LocalDateTime computedAt;


    public DynamicPriceRecord(){

    }

    public DynamicPriceRecord(Long eventId , Double computedPrice, String appliedRuleCodes) {
        this.eventId = eventId;
        this.computedPrice = computedPrice;
        this.appliedRuleCodes = appliedRuleCodes;
    }

    @PrePersist
    protected void onCreate(){
        computedAt = LocalDateTime.now();
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

    public void setEvent(Long eventId){
        this.eventId = eventId;
    }
    public Double getComputedPrice() {
        return computedPrice;
    }
    public void setComputedPrice(Double computedPrice){
        this.computedPrice = computedPrice;
    }
    public String getAppliedRuleCodes(){
        return appliedRuleCodes;
    }
    public void setAppliedRules(String appliedRuleCodes){
        this.appliedRuleCodes = appliedRuleCodes;
    }
    public LocalDateTime getComputedAt(){
        return computedAt;
    }
    public void setComputedAt(LocalDateTime computedAt){
        this.computedAt=computedAt;
    }
}