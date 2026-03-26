package com.medai.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientProfileRequest {
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodType;
    private String address;
    private String medicalHistory;
    private String allergies;
    private String chronicDiseases;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
