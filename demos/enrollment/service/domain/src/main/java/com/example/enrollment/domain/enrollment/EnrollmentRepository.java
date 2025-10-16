package com.example.enrollment.domain.enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(EnrollmentRecord enrollment);
    Optional<EnrollmentRecord> findById(EnrollmentId id);
    List<EnrollmentRecord> findAll();
}
