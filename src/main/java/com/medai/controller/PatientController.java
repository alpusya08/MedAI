package com.medai.controller;

import com.medai.dto.request.PatientProfileRequest;
import com.medai.dto.response.PatientProfileResponse;
import com.medai.service.PatientService;
import com.medai.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final SecurityUtils securityUtils;

    @GetMapping("/profile")
    public ResponseEntity<PatientProfileResponse> getProfile(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Getting patient profile for userId: {}", userId);

        PatientProfileResponse profile = patientService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<PatientProfileResponse> updateProfile(Authentication authentication, @RequestBody PatientProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Updating patient profile for userId: {}", userId);

        PatientProfileResponse profile = patientService.updateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }
}
