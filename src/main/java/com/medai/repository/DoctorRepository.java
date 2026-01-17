package com.medai.repository;

import com.medai.model.entity.Doctor;
import com.medai.model.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Optional<Patient> findByUserId(Long userId);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByVerifiedTrue();
    List<Doctor> findByVerifiedTrueOrderByRatingDesc();
}
