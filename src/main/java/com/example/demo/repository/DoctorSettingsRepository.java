package com.example.demo.repository;

import com.example.demo.model.DoctorSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorSettingsRepository extends JpaRepository<DoctorSettings, Long> {
}
