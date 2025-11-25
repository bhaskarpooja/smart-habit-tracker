package com.habittracker.service;

import com.habittracker.dto.AnalyticsResponse;
import com.habittracker.dto.AnalyticsResponse.WeeklyData;
import com.habittracker.model.Habit;
import com.habittracker.model.HabitLog;
import com.habittracker.repository.HabitLogRepository;
import com.habittracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    
    public AnalyticsResponse getAnalytics(Long userId) {
        List<Habit> habits = habitRepository.findByUserId(userId);
        if (habits.isEmpty()) {
            return AnalyticsResponse.builder()
                    .overallConsistency(0.0)
                    .totalStreak(0L)
                    .categoryStreaks(new HashMap<>())
                    .categoryConsistency(new HashMap<>())
                    .weeklyTrend(new ArrayList<>())
                    .heatmapData(new HashMap<>())
                    .build();
        }
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(365);
        
        List<HabitLog> allLogs = habitLogRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        
        Double overallConsistency = calculateOverallConsistency(allLogs);
        Long totalStreak = calculateTotalStreak(habits);
        Map<String, Long> categoryStreaks = calculateCategoryStreaks(habits);
        Map<String, Double> categoryConsistency = calculateCategoryConsistency(habits, allLogs);
        List<WeeklyData> weeklyTrend = calculateWeeklyTrend(allLogs);
        Map<String, Integer> heatmapData = calculateHeatmap(allLogs);
        
        return AnalyticsResponse.builder()
                .overallConsistency(overallConsistency)
                .totalStreak(totalStreak)
                .categoryStreaks(categoryStreaks)
                .categoryConsistency(categoryConsistency)
                .weeklyTrend(weeklyTrend)
                .heatmapData(heatmapData)
                .build();
    }
    
    private Double calculateOverallConsistency(List<HabitLog> logs) {
        if (logs.isEmpty()) return 0.0;
        long completed = logs.stream().filter(HabitLog::getStatus).count();
        return (double) completed / logs.size() * 100;
    }
    
    private Long calculateTotalStreak(List<Habit> habits) {
        return habits.stream()
                .mapToLong(h -> calculateHabitStreak(h.getId()))
                .max()
                .orElse(0L);
    }
    
    private Long calculateHabitStreak(Long habitId) {
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
    
    private Map<String, Long> calculateCategoryStreaks(List<Habit> habits) {
        Map<String, Long> categoryStreaks = new HashMap<>();
        
        Map<String, List<Habit>> habitsByCategory = habits.stream()
                .collect(Collectors.groupingBy(Habit::getCategory));
        
        for (Map.Entry<String, List<Habit>> entry : habitsByCategory.entrySet()) {
            long maxStreak = entry.getValue().stream()
                    .mapToLong(h -> calculateHabitStreak(h.getId()))
                    .max()
                    .orElse(0L);
            categoryStreaks.put(entry.getKey(), maxStreak);
        }
        
        return categoryStreaks;
    }
    
    private Map<String, Double> calculateCategoryConsistency(List<Habit> habits, List<HabitLog> allLogs) {
        Map<String, Double> categoryConsistency = new HashMap<>();
        
        Map<String, List<Habit>> habitsByCategory = habits.stream()
                .collect(Collectors.groupingBy(Habit::getCategory));
        
        for (Map.Entry<String, List<Habit>> entry : habitsByCategory.entrySet()) {
            List<Long> habitIds = entry.getValue().stream()
                    .map(Habit::getId)
                    .collect(Collectors.toList());
            
            List<HabitLog> categoryLogs = allLogs.stream()
                    .filter(log -> habitIds.contains(log.getHabit().getId()))
                    .collect(Collectors.toList());
            
            if (!categoryLogs.isEmpty()) {
                long completed = categoryLogs.stream().filter(HabitLog::getStatus).count();
                double consistency = (double) completed / categoryLogs.size() * 100;
                categoryConsistency.put(entry.getKey(), consistency);
            } else {
                categoryConsistency.put(entry.getKey(), 0.0);
            }
        }
        
        return categoryConsistency;
    }
    
    private List<WeeklyData> calculateWeeklyTrend(List<HabitLog> logs) {
        Map<String, List<HabitLog>> logsByWeek = logs.stream()
                .collect(Collectors.groupingBy(log -> getWeekKey(log.getDate())));
        
        List<WeeklyData> weeklyData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        for (Map.Entry<String, List<HabitLog>> entry : logsByWeek.entrySet()) {
            List<HabitLog> weekLogs = entry.getValue();
            long completed = weekLogs.stream().filter(HabitLog::getStatus).count();
            double consistency = weekLogs.isEmpty() ? 0.0 : (double) completed / weekLogs.size() * 100;
            
            weeklyData.add(WeeklyData.builder()
                    .week(entry.getKey())
                    .consistency(consistency)
                    .completedDays((int) completed)
                    .build());
        }
        
        weeklyData.sort(Comparator.comparing(WeeklyData::getWeek));
        return weeklyData.size() > 12 ? weeklyData.subList(weeklyData.size() - 12, weeklyData.size()) : weeklyData;
    }
    
    private String getWeekKey(LocalDate date) {
        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1);
        return weekStart.format(DateTimeFormatter.ofPattern("MMM dd"));
    }
    
    private Map<String, Integer> calculateHeatmap(List<HabitLog> logs) {
        Map<String, Integer> heatmap = new HashMap<>();
        
        for (HabitLog log : logs) {
            String dateKey = log.getDate().toString();
            heatmap.put(dateKey, log.getStatus() ? 1 : 0);
        }
        
        return heatmap;
    }
}

