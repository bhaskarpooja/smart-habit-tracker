package com.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIReportResponse {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String feedbackText;
    private LocalDateTime createdAt;
}

