package com.example.demo.dto;

import com.example.demo.model.ChatMessage;
import java.time.format.DateTimeFormatter;

public class ChatMessageDto {
    private Long id;
    private Long appointmentId;
    private Long senderId;
    private String text;
    private String time; // Formatted time for frontend display (e.g. "10:30 AM")
    private String from; // "patient" or "doctor" (to match the existing frontend logic if needed)

    public ChatMessageDto() {}

    public ChatMessageDto(ChatMessage message, String role) {
        this.id = message.getId();
        this.appointmentId = message.getAppointmentId();
        this.senderId = message.getSenderId();
        this.text = message.getText();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        this.time = message.getCreatedAt().format(formatter);
        this.from = role; 
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
}
