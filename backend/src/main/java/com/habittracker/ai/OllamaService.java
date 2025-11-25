package com.habittracker.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OllamaService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${ollama.model}")
    private String model;
    
    @Value("${ollama.timeout}")
    private int timeout;
    
    public OllamaService(@Value("${ollama.base-url}") String baseUrl, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    
    public String generateWeeklyCoachingReport(String habitData) {
        String prompt = String.format(
            "You are an expert productivity coach. Analyze this user's last 7 days of habits:\n\n%s\n\n" +
            "Give:\n1. Weekly summary\n2. Strengths\n3. Weak areas\n4. Personalized improvement plan\n5. Motivational message\n\n" +
            "Keep it short and friendly (max 300 words).",
            habitData
        );
        
        return generateText(prompt);
    }
    
    public String generateMotivationalMessage(String context) {
        String prompt = String.format(
            "Generate a short, friendly, and motivational message (max 50 words) for this context: %s",
            context
        );
        
        return generateText(prompt);
    }
    
    private String generateText(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            String response = webClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                String generatedText = jsonNode.get("response").asText();
                log.info("Ollama response received: {}", generatedText.substring(0, Math.min(100, generatedText.length())));
                return generatedText.trim();
            }
            
            return "Unable to generate AI response. Please check Ollama connection.";
        } catch (Exception e) {
            log.error("Error calling Ollama: {}", e.getMessage());
            return "AI service temporarily unavailable. Please try again later.";
        }
    }
}

