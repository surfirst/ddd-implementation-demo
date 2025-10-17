package com.example.enrollment.infrastructure.adapters.captcha;

import com.example.enrollment.domain.enrollmentprocess.ports.CaptchaServicePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockCaptchaService implements CaptchaServicePort {
    @Override
    public void validate(String captcha) {
        // Accept all in mock profile
    }
}
