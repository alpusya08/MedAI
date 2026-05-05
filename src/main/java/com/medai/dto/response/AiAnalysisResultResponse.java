package com.medai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysisResultResponse {
    private Long id;
    private String riskLevel;
    private Double confidenceLevel;
    private Integer prediction;
    private String urgency;
    private String explanation;
    private String disclaimer;
    private List<String> possibleConditions;
    private List<String> recommendedSpecialists;
    private List<String> recommendedTests;
    private LocalDateTime createdAt;
    private List<DoctorShortResponse> suggestedDoctors;
    private AiFeedbackResponse doctorFeedback;
}