package com.example.demo.dto;

import com.example.demo.model.User;

public class PatientProfileDto {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String bloodGroup;
    private String lastVisit;
    private String lastType;
    private String status;

    public PatientProfileDto() {}

    public PatientProfileDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.age = user.getAge() != null ? user.getAge() : 30;
        this.gender = user.getGender() != null ? user.getGender() : "Unknown";
        this.bloodGroup = user.getBloodGroup();
        this.lastVisit = user.getLastVisit() != null ? user.getLastVisit() : "Never";
        this.lastType = user.getLastType() != null ? user.getLastType().name() : "video";
        this.status = "Active";
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getGender() { return gender; }
    public String getBloodGroup() { return bloodGroup; }
    public String getLastVisit() { return lastVisit; }
    public String getLastType() { return lastType; }
    public String getStatus() { return status; }
}
