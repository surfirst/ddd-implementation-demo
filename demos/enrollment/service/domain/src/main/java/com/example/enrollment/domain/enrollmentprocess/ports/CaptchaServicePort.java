package com.example.enrollment.domain.enrollmentprocess.ports;

public interface CaptchaServicePort {
    void validate(String captcha);
}
