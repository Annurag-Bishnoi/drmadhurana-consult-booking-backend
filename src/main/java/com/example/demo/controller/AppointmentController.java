package com.example.demo.controller;

import com.example.demo.dto.AppointmentResponse;
import com.example.demo.dto.BookAppointmentRequest;
import com.example.demo.dto.UpdateNotesRequest;
import com.example.demo.dto.UpdatePrescriptionRequest;
import com.example.demo.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @RequestBody BookAppointmentRequest request,
            Authentication authentication) {
        Long patientId = Long.parseLong((String) authentication.getPrincipal());
        return ResponseEntity.ok(appointmentService.bookAppointment(patientId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(Authentication authentication) {
        Long patientId = Long.parseLong((String) authentication.getPrincipal());
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments(Authentication authentication) {
        // Here we could check if user has Admin role
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        // Normally check if user is Admin or authorized
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @PutMapping("/{id}/notes")
    public ResponseEntity<AppointmentResponse> updateNotes(
            @PathVariable Long id,
            @RequestBody UpdateNotesRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(appointmentService.updateNotes(id, request.getNotes()));
    }

    @PutMapping("/{id}/prescription")
    public ResponseEntity<AppointmentResponse> updatePrescription(
            @PathVariable Long id,
            @RequestBody UpdatePrescriptionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(appointmentService.updatePrescription(id, request.getPrescription()));
    }
}
