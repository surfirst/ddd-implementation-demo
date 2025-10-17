package com.example.enrollment.domain.enrollmentprocess.ports;

public interface MailServicePort {
    void send(String toEmail, String subject, String content);
}
