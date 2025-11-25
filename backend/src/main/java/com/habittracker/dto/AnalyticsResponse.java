package com.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Double overallConsistency;
    private Long totalStreak;
    private Map<String, Long> categoryStreaks;
    private Map<String, Double> categoryConsistency;
    private List<WeeklyData> weeklyTrend;
    private Map<String, Integer> heatmapData;
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyData {
        private String week;
        private Double consistency;
        private Integer completedDays;
    }
}



