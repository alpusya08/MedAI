package com.medai.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ai_analyses")
public class AiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // --- Входные данные от пациента ---
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

    // --- Результат от Python AI ---
    private String riskLevel;
    private Double confidenceLevel;
    private Integer prediction;
    private Double thresholdUsed;
    private String modelVersion;

    @Column(columnDefinition = "TEXT")
    private String urgency;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String possibleConditions;      // JSON: ["condition1", "condition2"]

    @Column(columnDefinition = "TEXT")
    private String recommendedSpecialists;  // JSON: ["Cardiologist"]

    @Column(columnDefinition = "TEXT")
    private String recommendedTests;        // JSON: ["ECG", "Lipid profile"]

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}