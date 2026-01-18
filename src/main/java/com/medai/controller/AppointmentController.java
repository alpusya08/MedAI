package com.medai.controller;

import com.medai.dto.request.CreateAppointmentRequest;
import com.medai.dto.request.UpdateAppointmentStatusRequest;
import com.medai.dto.response.AppointmentResponse;
import com.medai.service.AppointmentService;
import com.medai.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(Authentication authentication, @Valid @RequestBody CreateAppointmentRequest createAppointmentRequest) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Creating appointment for user: {}", userId);

        AppointmentResponse response = appointmentService.createAppointment(userId, createAppointmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        List<AppointmentResponse> appointments = appointmentService.getMyAppointments(userId);
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment(Authentication authentication, @PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        AppointmentResponse response = appointmentService.confirmAppointment(userId, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(Authentication authentication, @PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        AppointmentResponse response = appointmentService.cancelAppointment(userId, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(Authentication authentication, @PathVariable Long id, @Valid @RequestBody UpdateAppointmentStatusRequest request) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        AppointmentResponse response = appointmentService.completeAppointment(userId, id, request);
        return ResponseEntity.ok(response);
    }
}
