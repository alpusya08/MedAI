package com.medai.repository;

import com.medai.model.entity.DoctorFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorFeedbackRepository extends JpaRepository<DoctorFeedback, Long> {
    Optional<DoctorFeedback> findByAiAnalysisId(Long analysisId);
    boolean existsByAiAnalysisId(Long analysisId);
}