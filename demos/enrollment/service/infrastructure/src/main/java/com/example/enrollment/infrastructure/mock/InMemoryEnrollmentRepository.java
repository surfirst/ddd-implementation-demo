package com.example.enrollment.infrastructure.mock;

import com.example.enrollment.domain.enrollment.EnrollmentId;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import com.example.enrollment.domain.enrollment.EnrollmentRecord;
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
    private final Map<EnrollmentId, EnrollmentRecord> store = new ConcurrentHashMap<>();

    @Override
    public void save(EnrollmentRecord enrollment) {
        store.put(enrollment.getId(), enrollment);
    }

    @Override
    public Optional<EnrollmentRecord> findById(EnrollmentId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<EnrollmentRecord> findAll() {
        return new ArrayList<>(store.values());
    }
}
