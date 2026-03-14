package com.medai.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
}