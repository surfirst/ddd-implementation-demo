package com.example.enrollment.domain.enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    Optional<Enrollment> findById(EnrollmentId id);
    List<Enrollment> findAll();
}
