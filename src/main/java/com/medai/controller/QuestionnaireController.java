package com.medai.controller;

import com.medai.dto.request.QuestionnaireRequest;
import com.medai.dto.response.QuestionnaireResponse;
import com.medai.service.QuestionnaireService;
import com.medai.utils.SecurityUtils;
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
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<QuestionnaireResponse> submit(
            Authentication authentication,
            @RequestBody QuestionnaireRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Questionnaire submitted by userId: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionnaireService.submit(userId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<QuestionnaireResponse>> getMyQuestionnaires(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return ResponseEntity.ok(questionnaireService.getMyQuestionnaires(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionnaireResponse> getById(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return ResponseEntity.ok(questionnaireService.getById(userId, id));
    }
}
