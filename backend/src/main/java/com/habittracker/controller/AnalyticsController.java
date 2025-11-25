package com.habittracker.controller;

import com.habittracker.dto.AnalyticsResponse;
import com.habittracker.security.JwtTokenProvider;
import com.habittracker.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final JwtTokenProvider tokenProvider;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return tokenProvider.getUserIdFromToken(token);
    }
    
    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        AnalyticsResponse analytics = analyticsService.getAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }
}

