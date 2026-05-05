package com.medai.dto.response;

import com.medai.model.enums.FeedbackType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiFeedbackResponse {

    private Long id;
    private Long analysisId;
    private Long doctorId;
    private String doctorName;
    private FeedbackType feedbackType;
    private String notes;
    private String finalDiagnosis;
    private LocalDateTime createdAt;
}