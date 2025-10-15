package com.example.enrollment.infrastructure.mock;

import com.example.enrollment.domain.emailtemplates.EmailTemplate;
import com.example.enrollment.domain.emailtemplates.EmailTemplateRepository;
import com.example.enrollment.domain.shared.SupportedLanguage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Profile({"mock", "db"})
public class EmailTemplateRepositoryInMemory implements EmailTemplateRepository {
    private final Map<String, EmailTemplate> en = new HashMap<>();

    public EmailTemplateRepositoryInMemory() {
        // Seed some defaults for demo
        en.put(key("otp", SupportedLanguage.EN), new EmailTemplate("otp", "otp", "$$otp$$", SupportedLanguage.EN));
        en.put(key("welcome", SupportedLanguage.EN), new EmailTemplate("welcome", "welcome", "$$player_name$$|$$rank$$", SupportedLanguage.EN));
    }

    @Override
    public EmailTemplate findTemplateByNameAndLanguage(String name, SupportedLanguage language) {
        return en.get(key(name, language));
    }

    private String key(String name, SupportedLanguage lang) {
        return name + "::" + lang.name();
    }
}
