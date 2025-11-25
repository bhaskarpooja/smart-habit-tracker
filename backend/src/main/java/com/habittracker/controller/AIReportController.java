package com.habittracker.controller;

import com.habittracker.dto.AIReportResponse;
import com.habittracker.security.JwtTokenProvider;
import com.habittracker.service.AIReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AIReportController {
    private final AIReportService aiReportService;
    private final JwtTokenProvider tokenProvider;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return tokenProvider.getUserIdFromToken(token);
    }
    
    @GetMapping
    public ResponseEntity<List<AIReportResponse>> getUserReports(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<AIReportResponse> reports = aiReportService.getUserReports(userId);
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<AIReportResponse> getLatestReport(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        AIReportResponse report = aiReportService.getLatestReport(userId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}

