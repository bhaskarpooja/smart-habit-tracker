package com.habittracker.service;

import com.habittracker.dto.HabitRequest;
import com.habittracker.dto.HabitResponse;
import com.habittracker.model.Habit;
import com.habittracker.model.HabitLog;
import com.habittracker.model.User;
import com.habittracker.repository.HabitLogRepository;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public HabitResponse createHabit(Long userId, HabitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Habit habit = Habit.builder()
                .user(user)
                .title(request.getTitle())
                .category(request.getCategory())
                .build();
        
        habit = habitRepository.save(habit);
        return mapToResponse(habit);
    }
    
    public List<HabitResponse> getUserHabits(Long userId) {
        return habitRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public HabitResponse getHabitById(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return mapToResponse(habit);
    }
    
    @Transactional
    public HabitResponse updateHabit(Long habitId, Long userId, HabitRequest request) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        habit.setTitle(request.getTitle());
        habit.setCategory(request.getCategory());
        habit = habitRepository.save(habit);
        
        return mapToResponse(habit);
    }
    
    @Transactional
    public void deleteHabit(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        habitRepository.delete(habit);
    }
    
    @Transactional
    public void logHabit(Long habitId, Long userId, LocalDate date, Boolean status) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        HabitLog existingLog = habitLogRepository.findByHabitIdAndDate(habitId, date).orElse(null);
        
        if (existingLog != null) {
            existingLog.setStatus(status);
            habitLogRepository.save(existingLog);
        } else {
            HabitLog log = HabitLog.builder()
                    .habit(habit)
                    .date(date)
                    .status(status)
                    .build();
            habitLogRepository.save(log);
        }
    }
    
    public List<HabitLog> getHabitLogs(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return habitLogRepository.findByHabitId(habitId);
    }
    
    private HabitResponse mapToResponse(Habit habit) {
        Long currentStreak = calculateStreak(habit.getId());
        Double consistency = calculateConsistency(habit.getId());
        
        return HabitResponse.builder()
                .id(habit.getId())
                .title(habit.getTitle())
                .category(habit.getCategory())
                .currentStreak(currentStreak)
                .consistencyPercentage(consistency)
                .createdAt(habit.getCreatedAt())
                .build();
    }
    
    private Long calculateStreak(Long habitId) {
        List<HabitLog> logs = habitLogRepository.findByHabitId(habitId);
        if (logs.isEmpty()) return 0L;
        
        logs.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        long streak = 0;
        LocalDate currentDate = LocalDate.now();
        
        for (HabitLog log : logs) {
            if (log.getStatus() && (log.getDate().equals(currentDate) || log.getDate().equals(currentDate.minusDays(streak)))) {
                streak++;
                currentDate = log.getDate().minusDays(1);
            } else if (log.getDate().isBefore(currentDate)) {
                break;
            }
        }
        
        return streak;
    }
    
    private Double calculateConsistency(Long habitId) {
        List<HabitLog> logs = habitLogRepository.findByHabitId(habitId);
        if (logs.isEmpty()) return 0.0;
        
        long completed = logs.stream().filter(HabitLog::getStatus).count();
        return (double) completed / logs.size() * 100;
    }
}

