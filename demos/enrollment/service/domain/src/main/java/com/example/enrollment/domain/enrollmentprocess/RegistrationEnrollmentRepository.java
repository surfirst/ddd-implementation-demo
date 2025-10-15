package com.example.enrollment.domain.enrollmentprocess;

public interface RegistrationEnrollmentRepository {
    void addEnrollment(Enrollment enrollment);
    Enrollment getEnrollment(String id);
    void addEnrolledPlayer(String id, EnrolledPlayer enrolledPlayer);
}
