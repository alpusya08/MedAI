package com.medai.dto.response;

import com.medai.model.enums.AppointmentStatus;
import com.medai.model.enums.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;

    // Patient info
    private Long patientId;
    private String patientName;

    // Doctor info
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;

    private Long aiAnalysisId;

    // Appointment details
    private LocalDateTime appointmentDateTime;
    private AppointmentType type;
    private AppointmentStatus status;
    private String reasonForVisit;
    private String doctorNotes;
    private String diagnosis;
    private String prescription;
    private Double fee;

    private LocalDateTime createdAt;
}