package com.example.enrollment.application.enrollment;

import com.example.enrollment.domain.enrollment.EnrollmentRecord;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;

public class EnrollmentCommandService {
    private final EnrollmentRepository repository;

    public EnrollmentCommandService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public String create(String name) {
        EnrollmentRecord e = EnrollmentRecord.create(name);
        repository.save(e);
        return e.getId().getValue();
    }
}
