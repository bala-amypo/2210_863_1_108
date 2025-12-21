package com.example.demo.repository;

import com.example.demo.model.SeatInventoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SeatInventoryRecordRepository extends JpaRepository<SeatInventoryRecord, Long> {
    
    Optional<SeatInventoryRecord> findByEventId(Long eventId);
}