package com.medai.repository;

import com.medai.model.entity.Patient;

import java.util.Optional;

public interface PatientRepository {
    Optional<Patient> findByUserId(Long userId);
}
