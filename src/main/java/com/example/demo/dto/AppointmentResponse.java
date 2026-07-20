package com.example.demo.dto;

import com.example.demo.model.Appointment;

public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patient; // name
    private Integer age;
    private String type;
    private String date;
    private String time;
    private String status;
    private String reason;
    private Integer fee;
    private Integer duration;
    private String currency;
    private String region;
    private String meetingUrl;
    private String notes;
    private String prescription;

    public AppointmentResponse() {}

    public AppointmentResponse(Appointment appointment) {
        this.id = appointment.getId();
        this.patientId = appointment.getPatient().getId();
        this.patient = appointment.getPatient().getName();
        this.age = appointment.getPatient().getAge() != null ? appointment.getPatient().getAge() : 30;
        this.type = appointment.getType().name();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.status = appointment.getStatus().name();
        this.reason = appointment.getReason();
        this.fee = appointment.getFee();
        this.duration = appointment.getDuration();
        this.currency = appointment.getCurrency();
        this.region = appointment.getRegion();
        this.meetingUrl = appointment.getMeetingUrl();
        this.notes = appointment.getNotes();
        this.prescription = appointment.getPrescription();
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public String getPatient() { return patient; }
    public Integer getAge() { return age; }
    public String getType() { return type; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public Integer getFee() { return fee; }
    public void setFee(Integer fee) { this.fee = fee; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getMeetingUrl() { return meetingUrl; }
    public void setMeetingUrl(String meetingUrl) { this.meetingUrl = meetingUrl; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
}
