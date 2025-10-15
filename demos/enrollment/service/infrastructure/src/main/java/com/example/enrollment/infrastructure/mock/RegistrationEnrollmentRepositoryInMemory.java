package com.example.enrollment.infrastructure.mock;

import com.example.enrollment.domain.enrollmentprocess.EnrolledPlayer;
import com.example.enrollment.domain.enrollmentprocess.Enrollment;
import com.example.enrollment.domain.enrollmentprocess.RegistrationEnrollmentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("mock")
public class RegistrationEnrollmentRepositoryInMemory implements RegistrationEnrollmentRepository {
    private final Map<String, Enrollment> store = new ConcurrentHashMap<>();

    @Override
    public void addEnrollment(Enrollment enrollment) {
        store.put(enrollment.getId(), enrollment);
    }

    @Override
    public Enrollment getEnrollment(String id) {
        return store.get(id);
    }

    @Override
    public void addEnrolledPlayer(String id, EnrolledPlayer enrolledPlayer) {
        Enrollment e = store.get(id);
        if (e != null) {
            e.setEnrolledPlayer(enrolledPlayer);
        }
    }
}
