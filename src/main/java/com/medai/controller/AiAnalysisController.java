package com.medai.controller;

import com.medai.dto.request.AiAnalysisRequest;
import com.medai.dto.request.AiFeedbackRequest;
import com.medai.dto.response.AiAnalysisResultResponse;
import com.medai.dto.response.AiFeedbackResponse;
import com.medai.service.AiAnalysisService;
import com.medai.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;
    private final SecurityUtils securityUtils;

    @PostMapping("/analyze")
    public ResponseEntity<AiAnalysisResultResponse> analyze(
            Authentication authentication,
            @RequestBody AiAnalysisRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("AI analysis request from userId: {}", userId);
        return ResponseEntity.ok(aiAnalysisService.analyze(userId, request));
    }

    // История анализов пациента
    @GetMapping("/history")
    public ResponseEntity<List<AiAnalysisResultResponse>> getHistory(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return ResponseEntity.ok(aiAnalysisService.getHistory(userId));
    }

    // Получить конкретный анализ (врач смотрит перед приёмом)
    @GetMapping("/{analysisId}")
    public ResponseEntity<AiAnalysisResultResponse> getById(@PathVariable Long analysisId) {
        return ResponseEntity.ok(aiAnalysisService.getById(analysisId));
    }

    // Все анализы пациента (для врача/администратора)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AiAnalysisResultResponse>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(aiAnalysisService.getByPatientId(patientId));
    }

    // Врач оставляет фидбек на AI анализ
    @PostMapping("/{analysisId}/feedback")
    public ResponseEntity<AiFeedbackResponse> submitFeedback(
            Authentication authentication,
            @PathVariable Long analysisId,
            @Valid @RequestBody AiFeedbackRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(aiAnalysisService.submitFeedback(userId, analysisId, request));
    }
}