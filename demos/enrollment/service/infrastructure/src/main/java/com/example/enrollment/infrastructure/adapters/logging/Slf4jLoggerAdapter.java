package com.example.enrollment.infrastructure.adapters.logging;

import com.example.enrollment.domain.enrollmentprocess.ports.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLoggerAdapter implements Logger {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Slf4jLoggerAdapter.class);

    @Override
    public void warn(String message, Throwable t) {
        log.warn(message, t);
    }
}
