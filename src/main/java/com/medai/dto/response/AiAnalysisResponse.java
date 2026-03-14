package com.medai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AiAnalysisResponse {

    @JsonProperty("possible_conditions")
    private List<String> possibleConditions;

    @JsonProperty("risk_level")
    private String riskLevel;

    @JsonProperty("urgency")
    private String urgency;

    @JsonProperty("recommended_specialists")
    private List<String> recommendedSpecialists;

    @JsonProperty("recommended_tests")
    private List<String> recommendedTests;

    @JsonProperty("explanation")
    private String explanation;

    @JsonProperty("confidence_level")
    private Double confidenceLevel;

    @JsonProperty("prediction")
    private Integer prediction;

    @JsonProperty("model_version")
    private String modelVersion;

    @JsonProperty("threshold_used")
    private Double thresholdUsed;

    @JsonProperty("disclaimer")
    private String disclaimer;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}