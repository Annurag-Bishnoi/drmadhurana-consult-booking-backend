package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "doctor_settings")
public class DoctorSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_settings_slots", joinColumns = @JoinColumn(name = "doctor_settings_id"))
    @Column(name = "slot")
    private List<String> availableTimeSlots = new ArrayList<>();

    public DoctorSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<String> getAvailableTimeSlots() { return availableTimeSlots; }
    public void setAvailableTimeSlots(List<String> availableTimeSlots) { this.availableTimeSlots = availableTimeSlots; }
}
