package com.example.enrollment.infrastructure.adapters.mail;

import com.example.enrollment.domain.enrollmentprocess.ports.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class LoggingMailService implements MailService {
    private static final Logger log = LoggerFactory.getLogger(LoggingMailService.class);

    @Override
    public void send(String toEmail, String subject, String content) {
        log.info("[MAIL] to={}, subject={}, content={}", toEmail, subject, content);
    }
}
