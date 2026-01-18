package com.medai.service;

import com.medai.dto.request.DoctorProfileRequest;
import com.medai.dto.response.DoctorProfileResponse;
import com.medai.exception.BadRequestException;
import com.medai.exception.ResourceNotFoundException;
import com.medai.model.entity.Doctor;
import com.medai.model.entity.User;
import com.medai.repository.DoctorRepository;
import com.medai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    /**
     * Создание профиля врача при регистрации
     */
    public Doctor createDoctorProfile(Long userId) {
        log.info("Creating doctor profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (doctorRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException("Doctor profile already exists");
        }

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setVerified(false);
        doctor.setAcceptsOnlineAppointments(true);
        doctor.setAcceptsOfflineAppointments(true);
        doctor.setRating(0.0);

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor profile created with ID: {}", savedDoctor.getId());

        return savedDoctor;
    }

    /**
     * Обновление профиля врача
     */
    public DoctorProfileResponse updateProfile(Long userId, DoctorProfileRequest request) {
        log.info("Updating doctor profile for user ID: {}", userId);

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        doctor.setSpecialization(request.getSpecialization());
        doctor.setClinicName(request.getClinicName());
        doctor.setClinicAddress(request.getClinicAddress());
        doctor.setYearsOfExperience(request.getYearsOfExperience());
        doctor.setLicenceNumber(request.getLicenseNumber());
        doctor.setEducation(request.getEducation());
        doctor.setBiography(request.getBio());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setAcceptsOnlineAppointments(request.getAcceptsOnlineAppointments());
        doctor.setAcceptsOfflineAppointments(request.getAcceptsOfflineAppointments());

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor profile updated: {}", savedDoctor.getId());

        return mapToResponse(savedDoctor);
    }

    /**
     * Получение профиля врача
     */
    public DoctorProfileResponse getProfile(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

        return mapToResponse(doctor);
    }

    public List<DoctorProfileResponse> getAllVerifiedDoctors() {
        return doctorRepository.findByVerifiedTrueOrderByRatingDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorProfileResponse> findBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorProfileResponse> searchByName(String query) {
        return doctorRepository.searchByName(query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Маппинг Doctor -> DoctorProfileResponse
     */
    private DoctorProfileResponse mapToResponse(Doctor doctor) {
        return new DoctorProfileResponse(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getUser().getEmail(),
                doctor.getUser().getFirstName(),
                doctor.getUser().getLastName(),
                doctor.getUser().getPhoneNumber(),
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
                0,  // totalReviews - TODO: посчитать из таблицы reviews
                0,  // totalAppointments - TODO: посчитать из таблицы appointments
                doctor.getVerified(),
                true,  // active - TODO: добавить поле в entity
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }
}