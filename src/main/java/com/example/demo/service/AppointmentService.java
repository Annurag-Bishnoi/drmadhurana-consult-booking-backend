package com.example.demo.service;

import com.example.demo.dto.AppointmentResponse;
import com.example.demo.dto.BookAppointmentRequest;
import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import com.example.demo.model.enums.AppointmentStatus;
import com.example.demo.model.enums.AppointmentType;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DailyVideoService dailyVideoService;
    private final TwilioService twilioService;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository, DailyVideoService dailyVideoService, TwilioService twilioService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.dailyVideoService = dailyVideoService;
        this.twilioService = twilioService;
    }

    public AppointmentResponse bookAppointment(Long patientId, BookAppointmentRequest request) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        AppointmentType type = AppointmentType.valueOf(request.getType());
        String region = request.getRegion() != null ? request.getRegion() : "India";
        boolean isIndia = "India".equalsIgnoreCase(region);
        
        int baseFee = 0;
        int extraPer5 = 0;
        
        if (isIndia) {
            baseFee = (type == AppointmentType.video) ? 1000 : 500;
            extraPer5 = 100;
        } else {
            baseFee = (type == AppointmentType.video) ? 2000 : 1000;
            extraPer5 = 200;
        }
        
        int duration = request.getDuration() != null ? request.getDuration() : 15;
        int extraBlocks = Math.max(0, (duration - 15) / 5);
        int calculatedFee = baseFee + (extraBlocks * extraPer5);

        Appointment appointment = new Appointment(
                patient,
                type,
                request.getDate(),
                request.getTime(),
                request.getReason(),
                calculatedFee,
                duration,
                request.getCurrency() != null ? request.getCurrency() : "INR",
                region,
                request.getClinicLocation() != null ? request.getClinicLocation() : "Dehradun"
        );

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            patient.setPhoneNumber(request.getPhoneNumber());
            userRepository.save(patient);
        }

        Appointment saved = appointmentRepository.save(appointment);
        
        // Send SMS Confirmation asynchronously
        String phoneToSend = patient.getPhoneNumber();
        if (phoneToSend != null && !phoneToSend.isEmpty()) {
            String details = "Reminder: Appt " + request.getDate() + ", " + request.getTime() + ". Reply C to confirm or R to reschedule. Test message from Twilio.";
            new Thread(() -> twilioService.sendBookingConfirmation(phoneToSend, details)).start();
        }

        // Send Doctor Notification asynchronously
        String doctorDetails = "Reminder: Appt " + request.getDate() + ", " + request.getTime() + ". Reply C to confirm or R to reschedule. Test message from Twilio.";
        new Thread(() -> twilioService.sendDoctorNotification(doctorDetails)).start();

        return new AppointmentResponse(saved);
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByDateDesc(patientId)
                .stream()
                .map(AppointmentResponse::new)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAllByOrderByDateDesc()
                .stream()
                .map(AppointmentResponse::new)
                .collect(Collectors.toList());
    }

    public AppointmentResponse updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        try {
            AppointmentStatus newStatus = AppointmentStatus.valueOf(status.toLowerCase());
            appointment.setStatus(newStatus);
            
            // If starting a video/voice call and no meeting URL exists, create one using Daily.co
            if (newStatus == AppointmentStatus.in_progress && appointment.getMeetingUrl() == null) {
                if (appointment.getType() == AppointmentType.video || appointment.getType() == AppointmentType.voice) {
                    String roomUrl = dailyVideoService.createRoom();
                    if (roomUrl != null) {
                        appointment.setMeetingUrl(roomUrl);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        return new AppointmentResponse(appointmentRepository.save(appointment));
    }

    public AppointmentResponse updateNotes(Long id, String notes) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setNotes(notes);
        return new AppointmentResponse(appointmentRepository.save(appointment));
    }

    public AppointmentResponse updatePrescription(Long id, String prescription) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setPrescription(prescription);
        return new AppointmentResponse(appointmentRepository.save(appointment));
    }
}
