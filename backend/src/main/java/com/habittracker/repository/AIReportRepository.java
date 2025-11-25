package com.habittracker.repository;

import com.habittracker.model.AIReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIReportRepository extends JpaRepository<AIReport, Long> {
    List<AIReport> findByUserIdOrderByCreatedAtDesc(Long userId);
}

