package com.medai.model.entity;

import com.medai.model.enums.AppointmentStatus;
import com.medai.model.enums.AppointmentType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_analysis_id")
    private AiAnalysis aiAnalysis;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;  // Дата и время приема

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType type;  // ONLINE, OFFLINE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;  // PENDING, CONFIRMED, CANCELLED, COMPLETED

    @Column(columnDefinition = "TEXT")
    private String reasonForVisit;  // Причина обращения

    @Column(columnDefinition = "TEXT")
    private String doctorNotes;  // Заметки врача после приема

    @Column(columnDefinition = "TEXT")
    private String diagnosis;  // Диагноз

    @Column(columnDefinition = "TEXT")
    private String prescription;  // Назначения

    private Double fee;  // Стоимость консультации

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
