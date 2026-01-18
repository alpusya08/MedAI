package com.medai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String specialization;
    private String clinicName;
    private String clinicAddress;
    private Integer yearsOfExperience;
    private String licenseNumber;
    private String education;
    private String bio;
    private Double consultationFee;
    private Boolean acceptsOnlineAppointments;
    private Boolean acceptsOfflineAppointments;
    private Double rating;
    private Integer totalReviews;
    private Integer totalAppointments;
    private Boolean verified;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}