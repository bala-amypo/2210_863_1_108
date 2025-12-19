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

    public DynamicPriceRecord(Lon)
}