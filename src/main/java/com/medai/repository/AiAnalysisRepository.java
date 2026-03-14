package com.medai.repository;

import com.medai.model.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {
    List<AiAnalysis> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}