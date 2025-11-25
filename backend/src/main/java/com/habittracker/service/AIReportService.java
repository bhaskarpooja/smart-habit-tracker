package com.habittracker.service;

import com.habittracker.ai.OllamaService;
import com.habittracker.dto.AIReportResponse;
import com.habittracker.model.AIReport;
import com.habittracker.model.Habit;
import com.habittracker.model.HabitLog;
import com.habittracker.model.User;
import com.habittracker.repository.AIReportRepository;
import com.habittracker.repository.HabitLogRepository;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIReportService {
    private final AIReportRepository aiReportRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final OllamaService ollamaService;
    
    public List<AIReportResponse> getUserReports(Long userId) {
        return aiReportRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public AIReportResponse getLatestReport(Long userId) {
        List<AIReport> reports = aiReportRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (reports.isEmpty()) {
            return null;
        }
        return mapToResponse(reports.get(0));
    }
    
    @Transactional
    public void generateWeeklyReportForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        
        List<Habit> habits = habitRepository.findByUserId(userId);
        if (habits.isEmpty()) {
            log.info("No habits found for user {}, skipping report generation", userId);
            return;
        }
        
        StringBuilder habitData = new StringBuilder();
        for (Habit habit : habits) {
            List<HabitLog> logs = habitLogRepository.findByHabitIdAndDateBetween(
                    habit.getId(), startDate, endDate);
            
            long completed = logs.stream().filter(HabitLog::getStatus).count();
            double consistency = logs.isEmpty() ? 0.0 : (double) completed / logs.size() * 100;
            
            habitData.append(String.format("- %s (%s): %.1f%% consistency, %d/%d days completed\n",
                    habit.getTitle(), habit.getCategory(), consistency, completed, logs.size()));
        }
        
        String feedbackText = ollamaService.generateWeeklyCoachingReport(habitData.toString());
        
        AIReport report = AIReport.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .feedbackText(feedbackText)
                .build();
        
        aiReportRepository.save(report);
        log.info("Generated weekly AI report for user {}", userId);
    }
    
    public String generateMotivationalMessage(Long userId, String context) {
        return ollamaService.generateMotivationalMessage(context);
    }
    
    private AIReportResponse mapToResponse(AIReport report) {
        return AIReportResponse.builder()
                .id(report.getId())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .feedbackText(report.getFeedbackText())
                .createdAt(report.getCreatedAt())
                .build();
    }
}

