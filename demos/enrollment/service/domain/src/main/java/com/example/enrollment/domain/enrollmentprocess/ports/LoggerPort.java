package com.example.enrollment.domain.enrollmentprocess.ports;

public interface LoggerPort {
    void warn(String message, Throwable t);
}
