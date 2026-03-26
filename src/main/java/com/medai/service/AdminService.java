package com.medai.service;

import com.medai.dto.response.*;
import com.medai.exception.ResourceNotFoundException;
import com.medai.model.entity.Doctor;
import com.medai.model.entity.User;
import com.medai.model.entity.Appointment;
import com.medai.model.enums.AppointmentStatus;
import com.medai.model.enums.UserRole;
import com.medai.repository.AppointmentRepository;
import com.medai.repository.DoctorRepository;
import com.medai.repository.PatientRepository;
import com.medai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse toggleBlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setAccountNonLocked(!user.getAccountNonLocked());
        log.info("User {} is now {}", userId, user.getAccountNonLocked() ? "unblocked" : "blocked");

        return mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse changeUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(newRole);
        log.info("User {} role changed to {}", userId, newRole);

        return mapToUserResponse(userRepository.save(user));
    }

    public List<DoctorProfileResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDoctorProfileResponse)
                .toList();
    }

    @Transactional
    public DoctorProfileResponse toggleVerifyDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        doctor.setVerified(!doctor.getVerified());
        log.info("Doctor {} verified status changed to {}", doctorId, doctor.getVerified());

        return mapToDoctorProfileResponse(doctorRepository.save(doctor));
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToAppointmentResponse)
                .toList();
    }

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalDoctors = doctorRepository.count();
        long totalPatients = patientRepository.count();
        long verifiedDoctors = doctorRepository.findByVerifiedTrueOrderByRatingDesc().size();
        long pendingDoctors = totalDoctors - verifiedDoctors;
        long totalAppointments = appointmentRepository.count();
        long pending = appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long confirmed = appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count();
        long completed = appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long cancelled = appointmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();

        return new AdminStatsResponse(
                totalUsers, totalDoctors, totalPatients,
                verifiedDoctors, pendingDoctors,
                totalAppointments, pending, confirmed, completed, cancelled
        );
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getEnabled(),
                user.getAccountNonLocked(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private DoctorProfileResponse mapToDoctorProfileResponse(Doctor doctor) {
        User user = doctor.getUser();
        return new DoctorProfileResponse(
                doctor.getId(),
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                doctor.getSpecialization(),
                doctor.getClinicName(),
                doctor.getClinicAddress(),
                doctor.getYearsOfExperience(),
                doctor.getLicenceNumber(),
                doctor.getEducation(),
                doctor.getBiography(),
                doctor.getConsultationFee(),
                doctor.getAcceptsOnlineAppointments(),
                doctor.getAcceptsOfflineAppointments(),
                doctor.getRating(),
                doctor.getTotalReviews(),
                null, // totalAppointments - можно добавить позже
                doctor.getVerified(),
                user.getEnabled(),
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getSpecialization(),
                appointment.getAiAnalysis() != null ? appointment.getAiAnalysis().getId() : null,
                appointment.getAppointmentDateTime(),
                appointment.getType(),
                appointment.getStatus(),
                appointment.getReasonForVisit(),
                appointment.getDoctorNotes(),
                appointment.getDiagnosis(),
                appointment.getPrescription(),
                appointment.getFee(),
                appointment.getCreatedAt()
        );
    }
}