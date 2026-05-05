package com.medai.dto.request;

import lombok.Data;

@Data
public class QuestionnaireRequest {

    // --- Клинические поля для AI (подставляются из профиля если не переданы) ---
    private Integer age;
    private String sex;
    private String dataset;
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

    // --- Симптомы ---
    private Boolean shortnessOfBreath;
    private Boolean shortnessAtRest;
    private Boolean shortnessAtActivity;
    private Boolean palpitations;
    private Boolean dizziness;
    private Boolean swelling;

    // --- Временные характеристики ---
    private String symptomDuration;
    private String symptomFrequency;
    private String triggers;

    // --- Анамнез ---
    private Boolean previousCvd;
    private String cvdHistory;
    private Boolean familyHistoryCvd;
    private String chronicDiseases;
    private String medications;

    // --- Факторы риска ---
    private Boolean smoking;
    private String physicalActivity;
    private Double bmi;

    private String additionalNotes;
}