package com.example.enrollment.domain.emailtemplates;

import com.example.enrollment.domain.shared.SupportedLanguage;

public interface EmailTemplateRepository {
    EmailTemplate findTemplateByNameAndLanguage(String name, SupportedLanguage language);
}
