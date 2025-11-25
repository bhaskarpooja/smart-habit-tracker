package com.habittracker.repository;

import com.habittracker.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findByHabitId(Long habitId);
    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);
    List<HabitLog> findByHabitIdAndDateBetween(Long habitId, LocalDate start, LocalDate end);
    
    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit.user.id = :userId AND hl.date BETWEEN :start AND :end")
    List<HabitLog> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}

