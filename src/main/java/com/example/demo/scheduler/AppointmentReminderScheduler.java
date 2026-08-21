package com.example.demo.scheduler;

import com.example.demo.model.Appointment;
import com.example.demo.model.enums.AppointmentStatus;
import com.example.demo.model.enums.AppointmentType;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.service.TwilioService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final TwilioService twilioService;

    public AppointmentReminderScheduler(AppointmentRepository appointmentRepository, TwilioService twilioService) {
        this.appointmentRepository = appointmentRepository;
        this.twilioService = twilioService;
    }

    // Runs every 15 minutes
    @Scheduled(fixedRate = 900000)
    public void sendReminders() {
        System.out.println("Running Appointment Reminder Scheduler...");
        
        // Fetch upcoming appointments that haven't received a reminder
        List<Appointment> upcomingAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.upcoming)
                .filter(a -> !a.isReminderSent())
                .filter(a -> a.getType() == AppointmentType.video || a.getType() == AppointmentType.voice || a.getType() == AppointmentType.chat)
                .toList();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        for (Appointment appointment : upcomingAppointments) {
            try {
                LocalDate appointmentDate = LocalDate.parse(appointment.getDate());
                
                // Only process appointments for today
                if (appointmentDate.isEqual(today)) {
                    LocalTime appointmentTime = LocalTime.parse(appointment.getTime(), timeFormatter);
                    
                    // If the appointment is within the next 2 hours
                    if (appointmentTime.isAfter(now) && appointmentTime.minusHours(2).isBefore(now)) {
                        String phone = appointment.getPatient().getPhoneNumber();
                        if (phone != null && !phone.isEmpty()) {
                            // Determine meeting link based on type. 
                            // If video/voice, they might need the actual link or we tell them to join via dashboard.
                            String meetingLink = "the dashboard at " + "http://localhost:8081/patient/consultations";
                            if (appointment.getMeetingUrl() != null) {
                                meetingLink = appointment.getMeetingUrl();
                            }
                            
                            twilioService.sendWhatsAppReminder(phone, meetingLink);
                            
                            // Update flag
                            appointment.setReminderSent(true);
                            appointmentRepository.save(appointment);
                        }
                    }
                }
            } catch (DateTimeParseException e) {
                System.err.println("Failed to parse date/time for appointment ID: " + appointment.getId());
            }
        }
    }
}
