package com.medai.repository;

import com.medai.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByVerifiedTrueOrderByRatingDesc();
    List<Doctor> findBySpecializationContainingIgnoreCaseAndVerifiedTrue(String specialization);
    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "d.verified = true")
    List<Doctor> searchByName(@Param("query") String query);
}
