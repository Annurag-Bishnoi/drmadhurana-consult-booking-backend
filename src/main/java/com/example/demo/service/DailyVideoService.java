package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DailyVideoService {

    private final String apiKey;
    private final RestTemplate restTemplate;

    public DailyVideoService(@Value("${app.daily.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public String createRoom() {
        String url = "https://api.daily.co/v1/rooms";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Request body to create a room (we can use dynamic names or let Daily auto-generate)
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        
        // Room expires exactly 1 hour after creation to prevent dangling rooms
        long expTimestamp = (System.currentTimeMillis() / 1000) + 3600;
        properties.put("exp", expTimestamp); 
        
        // Automatically eject all participants when the room expires
        properties.put("eject_at_room_exp", true);
        
        // Limit to 2 people (Doctor and Patient) to prevent abuse and save bandwidth
        properties.put("max_participants", 2);

        body.put("properties", properties);
        // Let Daily auto-generate the room name to prevent conflicts

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("url");
            }
        } catch (Exception e) {
            System.err.println("Failed to create Daily room: " + e.getMessage());
        }
        return null;
    }
}
