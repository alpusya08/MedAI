package com.medai.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class DoctorProfileRequest {
    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization must be less than 100 characters")
    private String specialization;

    @Size(max = 200, message = "Clinic name must be less than 200 characters")
    private String clinicName;

    @Size(max = 500, message = "Clinic address must be less than 500 characters")
    private String clinicAddress;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 70, message = "Years of experience seems too high")
    private Integer yearsOfExperience;  // Лет опыта

    @Size(max = 50, message = "License number must be less than 50 characters")
    private String licenseNumber;  // Номер лицензии

    @Size(max = 1000, message = "Education must be less than 1000 characters")
    private String education;  // Образование

    @Size(max = 2000, message = "Bio must be less than 2000 characters")
    private String bio;  // Биография / О себе

    @DecimalMin(value = "0.0", message = "Consultation fee cannot be negative")
    @DecimalMax(value = "1000000.0", message = "Consultation fee seems too high")
    private Double consultationFee;  // Стоимость консультации

    private Boolean acceptsOnlineAppointments;  // Принимает онлайн

    private Boolean acceptsOfflineAppointments;  // Принимает оффлайн
}
