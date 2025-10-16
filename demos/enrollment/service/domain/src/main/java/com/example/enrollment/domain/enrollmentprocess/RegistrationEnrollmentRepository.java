package com.example.enrollment.domain.enrollmentprocess;

public interface RegistrationEnrollmentRepository {
    void addEnrollment(RegistrationEnrollment enrollment);
    RegistrationEnrollment getEnrollment(String id);
    void addEnrolledPlayer(String id, EnrolledPlayer enrolledPlayer);
}
