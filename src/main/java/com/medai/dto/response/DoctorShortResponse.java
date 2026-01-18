package com.medai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorShortResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String clinicName;
    private Integer yearsOfExperience;
    private Double rating;
    private Double consultationFee;
    private Boolean acceptsOnlineAppointments;
    private Boolean acceptsOfflineAppointments;
    private Boolean verified;
}
