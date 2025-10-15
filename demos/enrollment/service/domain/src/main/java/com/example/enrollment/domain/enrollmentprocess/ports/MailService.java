package com.example.enrollment.domain.enrollmentprocess.ports;

public interface MailService {
    void send(String toEmail, String subject, String content);
}
