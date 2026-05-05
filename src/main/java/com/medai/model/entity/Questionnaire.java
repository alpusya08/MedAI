package com.medai.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "questionnaires")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // --- Клинические поля для AI (UCI Heart Disease) ---
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

    // --- Симптомы пациента ---
    private Boolean shortnessOfBreath;
    private Boolean shortnessAtRest;
    private Boolean shortnessAtActivity;
    private Boolean palpitations;
    private Boolean dizziness;
    private Boolean swelling;

    // --- Временные характеристики ---
    @Column(columnDefinition = "TEXT")
    private String symptomDuration;

    @Column(columnDefinition = "TEXT")
    private String symptomFrequency;

    @Column(columnDefinition = "TEXT")
    private String triggers;

    // --- Анамнез ---
    private Boolean previousCvd;

    @Column(columnDefinition = "TEXT")
    private String cvdHistory;

    private Boolean familyHistoryCvd;

    @Column(columnDefinition = "TEXT")
    private String chronicDiseases;

    @Column(columnDefinition = "TEXT")
    private String medications;

    // --- Факторы риска ---
    private Boolean smoking;
    private String physicalActivity;
    private Double bmi;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @OneToOne(mappedBy = "questionnaire", fetch = FetchType.LAZY)
    private AiAnalysis aiAnalysis;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}