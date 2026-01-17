package com.medai.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "doctors")
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100, nullable = false)
    private String specialization;

    @Column(length = 50, nullable = false)
    private String licenceNumber;

    @Column(nullable = false)
    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(length = 200)
    private String clinicName;

    @Column(columnDefinition = "TEXT")
    private String clinicAddress;

    @Column(nullable = false)
    private Boolean acceptsOnlineAppointments;

    @Column(nullable = false)
    private Boolean acceptsOfflineAppointments;

    @Column
    private Double consultationFee;

    @Column Double rating; // Рейтинг врача (0.0 - 5.0)

    @Column
    private Integer totalReviews; // Количество отзывов

    @Column(nullable = false)
    private Boolean verified = false; // Подтвержден ли администратором

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime updatedAt;
}
