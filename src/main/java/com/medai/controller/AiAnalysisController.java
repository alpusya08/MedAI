package com.medai.controller;

import com.medai.dto.request.AiAnalysisRequest;
import com.medai.dto.response.AiAnalysisResultResponse;
import com.medai.service.AiAnalysisService;
import com.medai.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}