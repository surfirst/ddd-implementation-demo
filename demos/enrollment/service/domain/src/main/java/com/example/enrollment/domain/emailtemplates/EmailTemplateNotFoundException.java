package com.example.enrollment.domain.emailtemplates;

public class EmailTemplateNotFoundException extends RuntimeException {
    public EmailTemplateNotFoundException(String name) {
        super("Email template not found: " + name);
    }
}
