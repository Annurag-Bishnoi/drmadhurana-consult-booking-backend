package com.example.demo.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Value("${twilio.whatsapp.number}")
    private String twilioWhatsappNumber;

    @Value("${twilio.doctor.phone:}")
    private String doctorPhone;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendBookingConfirmation(String toPhone, String details) {
        if (toPhone == null || toPhone.isEmpty()) return;
        
        // Ensure toPhone is in E.164 format. E.g., if it starts with 0 or doesn't have a plus. 
        // For simplicity, we assume frontend provides it correctly or we prepend a default country code if missing.
        String formattedPhone = formatPhone(toPhone);
        
        try {
            Message.creator(
                new PhoneNumber(formattedPhone),
                new PhoneNumber(twilioPhoneNumber),
                details
            ).create();
            System.out.println("SMS sent successfully to " + formattedPhone);
        } catch (Exception e) {
            System.err.println("Failed to send SMS to " + formattedPhone + ": " + e.getMessage());
        }
    }

    public void sendDoctorNotification(String details) {
        if (doctorPhone == null || doctorPhone.isEmpty()) return;
        
        String formattedPhone = formatPhone(doctorPhone);
        
        try {
            Message.creator(
                new PhoneNumber(formattedPhone),
                new PhoneNumber(twilioPhoneNumber),
                details
            ).create();
            System.out.println("Doctor notification SMS sent successfully to " + formattedPhone);
        } catch (Exception e) {
            System.err.println("Failed to send doctor notification SMS: " + e.getMessage());
        }
    }

    public void sendWhatsAppReminder(String toPhone, String meetingUrl) {
        if (toPhone == null || toPhone.isEmpty()) return;
        
        String formattedPhone = "whatsapp:" + formatPhone(toPhone);
        
        try {
            Message.creator(
                new PhoneNumber(formattedPhone),
                new PhoneNumber(twilioWhatsappNumber),
                "Reminder: Your consultation with Dr. Madhu is starting soon. Join here: " + meetingUrl
            ).create();
            System.out.println("WhatsApp message sent successfully to " + formattedPhone);
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message to " + formattedPhone + ": " + e.getMessage());
        }
    }
    
    private String formatPhone(String phone) {
        phone = phone.trim();
        if (!phone.startsWith("+")) {
            // Default to India country code if not provided
            phone = "+91" + phone; 
        }
        return phone;
    }
}
