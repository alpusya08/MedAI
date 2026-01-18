package com.medai.repository;

import com.medai.model.entity.Appointment;
import com.medai.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Все записи пациента
    List<Appointment> findByPatientId(Long patientId);

    // Все записи врача
    List<Appointment> findByDoctorId(Long doctorId);

    // Записи пациента по статусу
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    // Записи врача по статусу
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    // Предстоящие записи врача
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDateTime > :now " +
            "AND a.status IN ('PENDING', 'CONFIRMED') " +
            "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(
            @Param("doctorId") Long doctorId,
            @Param("now") LocalDateTime now
    );

    // Проверка занятости слота
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentDateTime = :dateTime " +
            "AND a.status IN ('PENDING', 'CONFIRMED')")
    boolean isSlotTaken(
            @Param("doctorId") Long doctorId,
            @Param("dateTime") LocalDateTime dateTime
    );
}
