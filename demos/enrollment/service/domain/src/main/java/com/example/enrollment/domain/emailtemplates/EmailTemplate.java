package com.example.enrollment.domain.emailtemplates;

import com.example.enrollment.domain.shared.SupportedLanguage;

public final class EmailTemplate {
    private final String name;
    private final String subject;
    private final String body;
    private final SupportedLanguage language;

    public EmailTemplate(String name, String subject, String body, SupportedLanguage language) {
        this.name = name;
        this.subject = subject;
        this.body = body;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public SupportedLanguage getLanguage() {
        return language;
    }
}
