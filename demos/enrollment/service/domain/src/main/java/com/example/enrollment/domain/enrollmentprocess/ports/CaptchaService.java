package com.example.enrollment.domain.enrollmentprocess.ports;

public interface CaptchaService {
    void validate(String captcha);
}
