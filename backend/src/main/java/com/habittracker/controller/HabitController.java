package com.habittracker.controller;

import com.habittracker.dto.HabitLogRequest;
import com.habittracker.dto.HabitRequest;
import com.habittracker.dto.HabitResponse;
import com.habittracker.security.JwtTokenProvider;
import com.habittracker.service.HabitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class HabitController {
    private final HabitService habitService;
    private final JwtTokenProvider tokenProvider;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return tokenProvider.getUserIdFromToken(token);
    }
    
    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(@Valid @RequestBody HabitRequest request, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        HabitResponse response = habitService.createHabit(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<HabitResponse>> getUserHabits(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<HabitResponse> habits = habitService.getUserHabits(userId);
        return ResponseEntity.ok(habits);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getHabit(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        HabitResponse habit = habitService.getHabitById(id, userId);
        return ResponseEntity.ok(habit);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(@PathVariable Long id, @Valid @RequestBody HabitRequest request, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        HabitResponse response = habitService.updateHabit(id, userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        habitService.deleteHabit(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/logs")
    public ResponseEntity<Void> logHabit(@PathVariable Long id, @Valid @RequestBody HabitLogRequest request, HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        habitService.logHabit(id, userId, request.getDate(), request.getStatus());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/logs")
    public ResponseEntity<?> getHabitLogs(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(habitService.getHabitLogs(id, userId));
    }
}

