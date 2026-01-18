package com.medai.service;

import com.medai.dto.request.CreateAppointmentRequest;
import com.medai.dto.request.UpdateAppointmentStatusRequest;
import com.medai.dto.response.AppointmentResponse;
import com.medai.exception.BadRequestException;
import com.medai.exception.ResourceNotFoundException;
import com.medai.model.entity.Appointment;
import com.medai.model.entity.Doctor;
import com.medai.model.entity.Patient;
import com.medai.model.entity.User;
import com.medai.model.enums.AppointmentStatus;
import com.medai.model.enums.AppointmentType;
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
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AppointmentResponse createAppointment(Long patientUserId, CreateAppointmentRequest request) {

        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (!doctor.getVerified()) {
            throw new BadRequestException("Doctor is not verified");
        }

        if (appointmentRepository.isSlotTaken(doctor.getId(), request.getAppointmentDateTime())) {
            throw new BadRequestException("Slot is already taken");
        }

        if (request.getType() == AppointmentType.ONLINE && !doctor.getAcceptsOnlineAppointments()) {
            throw new  BadRequestException("Doctor doesn't accepts online appointments");
        }

        if (request.getType() == AppointmentType.OFFLINE && !doctor.getAcceptsOfflineAppointments()) {
            throw new  BadRequestException("Doctor doesn't accepts offline appointments");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setType(request.getType());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setReasonForVisit(request.getReasonForVisit());
        appointment.setFee(doctor.getConsultationFee());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created with ID: {}", savedAppointment.getId());

        return mapToResponse(savedAppointment);
    }

    public List<AppointmentResponse> getMyAppointments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == UserRole.PATIENT) {
            Patient patient = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

            return appointmentRepository.findByPatientId(patient.getId())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } else if (user.getRole() == UserRole.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

            return appointmentRepository.findByDoctorId(doctor.getId())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } else {
            throw new BadRequestException("Invalid user role for appointments");
        }
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return appointmentRepository.findByPatientId(patient.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorUserId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        return appointmentRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse confirmAppointment(Long doctorUserId, Long appointmentId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("This appointment does not belong to you");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Only pending appointments can be confirmed");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    public AppointmentResponse cancelAppointment(Long userId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        boolean isPatient = appointment.getPatient().getUser().getId().equals(userId);
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(userId);

        if (!isPatient && !isDoctor) {
            throw new BadRequestException("You don't have permission to cancel this appointment");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    public AppointmentResponse completeAppointment(Long doctorUserId, Long appointmentId, UpdateAppointmentStatusRequest request) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("This appointment does not belong to you");
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed appointments can be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDoctorNotes(request.getDoctorNotes());
        appointment.setDiagnosis(request.getDiagnosis());
        appointment.setPrescription(request.getPrescription());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }



    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getFirstName() + " " +
                        appointment.getPatient().getUser().getLastName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getFirstName() + " " +
                        appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getSpecialization(),
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
