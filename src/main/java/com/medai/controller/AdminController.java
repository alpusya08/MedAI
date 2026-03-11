package com.medai.controller;

import com.medai.dto.response.*;
import com.medai.model.enums.UserRole;
import com.medai.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<UserResponse> toggleBlockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleBlockUser(id));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role
    ) {
        return ResponseEntity.ok(adminService.changeUserRole(id, role));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorProfileResponse>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @PatchMapping("/doctors/{id}/verify")
    public ResponseEntity<DoctorProfileResponse> toggleVerifyDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleVerifyDoctor(id));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(adminService.getAllAppointments());
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}