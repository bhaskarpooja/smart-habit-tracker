package com.habittracker.scheduler;

import com.habittracker.repository.UserRepository;
import com.habittracker.service.AIReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIReportScheduler {
    private final AIReportService aiReportService;
    private final UserRepository userRepository;
    
    @Scheduled(cron = "0 0 9 * * SUN") // Every Sunday at 9 AM
    public void generateWeeklyReports() {
        log.info("Starting weekly AI report generation...");
        
        userRepository.findAll().forEach(user -> {
            try {
                aiReportService.generateWeeklyReportForUser(user.getId());
            } catch (Exception e) {
                log.error("Error generating report for user {}: {}", user.getId(), e.getMessage());
            }
        });
        
        log.info("Completed weekly AI report generation");
    }
}

