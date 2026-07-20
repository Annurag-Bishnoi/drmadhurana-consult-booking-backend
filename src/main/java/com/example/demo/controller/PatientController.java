package com.example.demo.controller;

import com.example.demo.dto.MedicalDocumentDto;
import com.example.demo.dto.PatientProfileDto;
import com.example.demo.model.MedicalDocument;
import com.example.demo.model.User;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.MedicalDocumentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PatientController {

    private final UserRepository userRepository;
    private final MedicalDocumentRepository documentRepository;

    public PatientController(UserRepository userRepository, MedicalDocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientProfileDto>> getAllPatients(Authentication authentication) {
        // Typically check if admin/doctor here
        List<PatientProfileDto> patients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.PATIENT)
                .map(PatientProfileDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<MedicalDocumentDto>> getDocuments(
            @RequestParam(required = false) Long patientId,
            Authentication authentication) {
        
        Long userId = Long.parseLong((String) authentication.getPrincipal());
        
        // If patientId is not provided, fetch for the currently logged in user
        Long targetId = patientId != null ? patientId : userId;

        List<MedicalDocumentDto> docs = documentRepository.findByPatientIdOrderByUploadedAtDesc(targetId)
                .stream()
                .map(MedicalDocumentDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(docs);
    }

    @PostMapping("/documents")
    public ResponseEntity<MedicalDocumentDto> uploadDocument(
            @RequestBody java.util.Map<String, String> payload,
            Authentication authentication) {
        String name = payload.get("name");
        String size = payload.get("size");
        String data = payload.get("data");
        
        Long patientId = Long.parseLong((String) authentication.getPrincipal());
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Format current date as "12 Jul"
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM"));

        MedicalDocument doc = new MedicalDocument(patient, name, size, date, data);
        doc = documentRepository.save(doc);

        return ResponseEntity.ok(new MedicalDocumentDto(doc));
    }
}
