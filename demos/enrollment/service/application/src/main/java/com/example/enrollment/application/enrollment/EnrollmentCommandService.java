package com.example.enrollment.application.enrollment;

import com.example.enrollment.domain.enrollment.Enrollment;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;

public class EnrollmentCommandService {
    private final EnrollmentRepository repository;

    public EnrollmentCommandService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public String create(String name) {
        Enrollment e = Enrollment.create(name);
        repository.save(e);
        return e.getId().getValue();
    }
}
