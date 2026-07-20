package com.example.demo.controller;

import com.example.demo.model.DoctorSettings;
import com.example.demo.repository.DoctorSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class DoctorSettingsController {

    private final DoctorSettingsRepository doctorSettingsRepository;

    public DoctorSettingsController(DoctorSettingsRepository doctorSettingsRepository) {
        this.doctorSettingsRepository = doctorSettingsRepository;
    }

    @GetMapping("/slots")
    public ResponseEntity<List<String>> getSlots() {
        DoctorSettings settings = getSettings();
        return ResponseEntity.ok(settings.getAvailableTimeSlots());
    }

    @PutMapping("/slots")
    public ResponseEntity<List<String>> updateSlots(
            @RequestBody Map<String, List<String>> request,
            Authentication authentication) {
        
        // normally check if authentication belongs to a doctor/admin
        DoctorSettings settings = getSettings();
        List<String> newSlots = request.get("slots");
        
        if (newSlots != null) {
            settings.setAvailableTimeSlots(newSlots);
            doctorSettingsRepository.save(settings);
        }
        
        return ResponseEntity.ok(settings.getAvailableTimeSlots());
    }

    private DoctorSettings getSettings() {
        List<DoctorSettings> all = doctorSettingsRepository.findAll();
        if (all.isEmpty()) {
            DoctorSettings settings = new DoctorSettings();
            // Default slots
            settings.setAvailableTimeSlots(Arrays.asList(
                    "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
                    "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
                    "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM",
                    "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM"
            ));
            return doctorSettingsRepository.save(settings);
        }
        return all.get(0);
    }
}
