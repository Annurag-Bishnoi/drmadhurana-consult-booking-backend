package com.example.demo.controller;

import com.example.demo.model.DoctorSettings;
import com.example.demo.model.Appointment;
import com.example.demo.repository.DoctorSettingsRepository;
import com.example.demo.repository.AppointmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/settings")
public class DoctorSettingsController {

    private final DoctorSettingsRepository doctorSettingsRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorSettingsController(DoctorSettingsRepository doctorSettingsRepository, AppointmentRepository appointmentRepository) {
        this.doctorSettingsRepository = doctorSettingsRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/slots")
    public ResponseEntity<List<String>> getSlots(@RequestParam(required = false) String date, @RequestParam(required = false) String location) {
        DoctorSettings settings = getSettings();
        List<String> available;
        
        if (location != null && !location.trim().isEmpty() && !location.equalsIgnoreCase("Online")) {
            String locationSlotsStr = settings.getLocationSlots().get(location.trim());
            if (locationSlotsStr != null && !locationSlotsStr.trim().isEmpty()) {
                available = Arrays.asList(locationSlotsStr.split(",")).stream().map(String::trim).collect(Collectors.toList());
            } else {
                available = new ArrayList<>();
            }
        } else {
            available = settings.getAvailableTimeSlots();
        }
        
        if (date != null && !date.isEmpty()) {
            List<Appointment> booked = appointmentRepository.findByDate(date);
            List<String> bookedTimes = booked.stream()
                .map(Appointment::getTime)
                .collect(Collectors.toList());
            available = available.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(available);
    }

    @GetMapping("/all")
    public ResponseEntity<DoctorSettings> getAllSettings() {
        return ResponseEntity.ok(getSettings());
    }

    @PutMapping("/all")
    public ResponseEntity<DoctorSettings> updateAllSettings(@RequestBody DoctorSettings request) {
        DoctorSettings settings = getSettings();
        if (request.getAvailableTimeSlots() != null) {
            settings.setAvailableTimeSlots(request.getAvailableTimeSlots());
        }
        if (request.getLocationSlots() != null) {
            settings.setLocationSlots(request.getLocationSlots());
        }
        return ResponseEntity.ok(doctorSettingsRepository.save(settings));
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
