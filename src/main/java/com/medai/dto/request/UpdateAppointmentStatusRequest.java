package com.medai.dto.request;

import com.medai.model.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {
    @NotNull
    private AppointmentStatus status;

    private String doctorNotes;
    private String diagnosis;
    private String prescription;
}
