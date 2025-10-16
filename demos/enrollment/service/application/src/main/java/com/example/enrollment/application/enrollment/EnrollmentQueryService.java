package com.example.enrollment.application.enrollment;

import com.example.enrollment.domain.enrollment.EnrollmentId;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import com.example.enrollment.domain.enrollment.EnrollmentRecord;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnrollmentQueryService {
    private final EnrollmentRepository repository;

    public EnrollmentQueryService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public List<EnrollmentView> listAll() {
        return repository.findAll().stream()
                .map(EnrollmentView::from)
                .collect(Collectors.toList());
    }

    public Optional<EnrollmentView> getById(String id) {
        return repository.findById(EnrollmentId.fromString(id)).map(EnrollmentView::from);
    }

    public record EnrollmentView(String id, String name, Instant createdAt) {
        public static EnrollmentView from(EnrollmentRecord e) {
            return new EnrollmentView(e.getId().getValue(), e.getName(), e.getCreatedAt());
        }
    }
}
