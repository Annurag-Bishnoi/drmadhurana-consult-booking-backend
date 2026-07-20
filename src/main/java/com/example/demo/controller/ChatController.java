package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
import com.example.demo.dto.SendMessageRequest;
import com.example.demo.model.Appointment;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public ChatController(ChatMessageRepository chatMessageRepository, 
                          AppointmentRepository appointmentRepository,
                          UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<List<ChatMessageDto>> getMessages(@PathVariable Long appointmentId, Authentication authentication) {
        // Find appointment to ensure it exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        List<ChatMessage> messages = chatMessageRepository.findByAppointmentIdOrderByCreatedAtAsc(appointmentId);
        
        List<ChatMessageDto> dtos = messages.stream().map(msg -> {
            // Determine role of sender
            User sender = userRepository.findById(msg.getSenderId()).orElse(null);
            String role = (sender != null && sender.getRole() == UserRole.PATIENT) ? "patient" : "doctor";
            return new ChatMessageDto(msg, role);
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{appointmentId}")
    public ResponseEntity<ChatMessageDto> sendMessage(@PathVariable Long appointmentId, 
                                                      @RequestBody SendMessageRequest request,
                                                      Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        ChatMessage message = new ChatMessage(appointmentId, userId, request.getText());
        ChatMessage saved = chatMessageRepository.save(message);

        String role = user.getRole() == UserRole.PATIENT ? "patient" : "doctor";
        return ResponseEntity.ok(new ChatMessageDto(saved, role));
    }
}
