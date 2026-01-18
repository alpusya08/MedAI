package com.medai.controller;

import com.medai.dto.request.DoctorProfileRequest;
import com.medai.dto.response.DoctorProfileResponse;
import com.medai.service.DoctorService;
import com.medai.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {

    private final DoctorService doctorService;
    private final SecurityUtils securityUtils;

    @GetMapping("/profile")
    public ResponseEntity<DoctorProfileResponse> getProfile(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Getting doctor profile for userId: {}", userId);

        DoctorProfileResponse profile = doctorService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Обновить профиль врача
     * PUT /api/doctors/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<DoctorProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody DoctorProfileRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Updating doctor profile for userId: {}", userId);

        DoctorProfileResponse profile = doctorService.updateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<List<DoctorProfileResponse>> getAllDoctors() {
        log.info("Getting all verified doctors");
        List<DoctorProfileResponse> doctors = doctorService.getAllVerifiedDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorProfileResponse>> getDoctorsBySpecialization(
            @PathVariable String specialization
    ) {
        log.info("Getting doctors by specialization: {}", specialization);
        List<DoctorProfileResponse> doctors = doctorService.findBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorProfileResponse>> searchDoctors(
            @RequestParam("q") String query
    ) {
        log.info("Searching doctors with query: {}", query);
        List<DoctorProfileResponse> doctors = doctorService.searchByName(query);
        return ResponseEntity.ok(doctors);
    }
}

