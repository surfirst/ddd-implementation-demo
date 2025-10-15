package com.example.enrollment.domain.exceptions;

public class InvalidCaptchaException extends RuntimeException {
    public InvalidCaptchaException(String message) { super(message); }
}
