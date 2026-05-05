package com.medai.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionnaireResponse {

    private Long id;
    private Long patientId;

    // Clinical fields
    private Integer age;
    private String sex;
    private String cp;
    private Integer trestbps;
    private Integer chol;
    private Boolean fbs;
    private String restecg;
    private Integer thalch;
    private Boolean exang;
    private Double oldpeak;
    private String slope;
    private Integer ca;
    private String thal;

    // Symptoms
    private Boolean shortnessOfBreath;
    private Boolean shortnessAtRest;
    private Boolean shortnessAtActivity;
    private Boolean palpitations;
    private Boolean dizziness;
    private Boolean swelling;

    // Temporal
    private String symptomDuration;
    private String symptomFrequency;
    private String triggers;

    // Anamnesis
    private Boolean previousCvd;
    private String cvdHistory;
    private Boolean familyHistoryCvd;
    private String chronicDiseases;
    private String medications;

    // Risk factors
    private Boolean smoking;
    private String physicalActivity;
    private Double bmi;

    private String additionalNotes;

    private AiAnalysisResultResponse aiAnalysis;

    private LocalDateTime createdAt;
}