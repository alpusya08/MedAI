package com.medai.service;

import com.medai.dto.request.PatientProfileRequest;
import com.medai.dto.response.PatientProfileResponse;
import com.medai.exception.BadRequestException;
import com.medai.exception.ResourceNotFoundException;
import com.medai.model.entity.Patient;
import com.medai.model.entity.User;
import com.medai.repository.PatientRepository;
import com.medai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public Patient createPatientProfile(Long userId) {
        log.info("Create Patient Profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (patientRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException("Patient profile already exists");
        }

        Patient patient = new Patient();
        patient.setUser(user);

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient profile created with ID: {}", savedPatient.getId());

        return savedPatient;
    }

    public PatientProfileResponse updateProfile(Long userId, PatientProfileRequest request) {
        log.info("Update Patient Profile for user ID: {}", userId);

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setBloodType(request.getBloodType());
        patient.setAddress(request.getAddress());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setAllergies(request.getAllergies());
        patient.setChronicDiseases(request.getChronicDiseases());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient profile update: {}", savedPatient.getId());

        return mapToResponse(savedPatient);
    }

    public PatientProfileResponse getProfile(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return mapToResponse(patient);
    }

    private PatientProfileResponse mapToResponse(Patient patient) {
        return new PatientProfileResponse(
                patient.getId(),
                patient.getUser().getId(),
                patient.getUser().getEmail(),
                patient.getUser().getFirstName(),
                patient.getUser().getLastName(),
                patient.getUser().getPhoneNumber(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getBloodType(),
                patient.getAddress(),
                patient.getMedicalHistory(),
                patient.getAllergies(),
                patient.getChronicDiseases(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone()
        );
    }
}
