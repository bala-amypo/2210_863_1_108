package com.example.demo.repository;

import com.example.demo.model.DynamicPriceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicPriceRecordRepository extends JpaRepository<DynamicPriceRecord, Long> {
    
    List<DynamicPriceRecord> findByEventIdOrderByComputedAtDesc(Long eventId);
    
    Optional<DynamicPriceRecord> findFirstByEventIdOrderByComputedAtDesc(Long eventId);
}