package com.example.enrollment.infrastructure.mock;

import com.example.enrollment.domain.enrollment.Enrollment;
import com.example.enrollment.domain.enrollment.EnrollmentId;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("mock")
public class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private final Map<EnrollmentId, Enrollment> store = new ConcurrentHashMap<>();

    @Override
    public void save(Enrollment enrollment) {
        store.put(enrollment.getId(), enrollment);
    }

    @Override
    public Optional<Enrollment> findById(EnrollmentId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Enrollment> findAll() {
        return new ArrayList<>(store.values());
    }
}
