package com.example.demo.repository;

import com.example.demo.model.PriceAdjustmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriceAdjustmentLogRepository extends JpaRepository<PriceAdjustmentLog, Long> {
    
    List<PriceAdjustmentLog> findByEventId(Long eventId);
}