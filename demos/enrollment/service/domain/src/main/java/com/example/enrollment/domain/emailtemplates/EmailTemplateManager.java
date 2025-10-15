package com.example.enrollment.domain.emailtemplates;

import com.example.enrollment.domain.shared.SupportedLanguage;

import java.util.*;

public class EmailTemplateManager {
    private final EmailTemplateRepository repository;
    private final SupportedLanguage defaultLanguage;
    private final Map<String, List<EmailTemplateRenderer>> renderersByKey = new HashMap<>();

    public EmailTemplateManager(EmailTemplateRepository repository, SupportedLanguage defaultLanguage) {
        this.repository = Objects.requireNonNull(repository);
        this.defaultLanguage = Objects.requireNonNull(defaultLanguage);
    }

    public void addRenderer(String templateKey, EmailTemplateRenderer renderer) {
        renderersByKey.computeIfAbsent(templateKey, k -> new ArrayList<>()).add(renderer);
    }

    public RenderedEmail render(String templateKey, Map<String, Object> parameters, SupportedLanguage preferredLanguage) {
        EmailTemplate t = repository.findTemplateByNameAndLanguage(templateKey, preferredLanguage);
        if (t == null) {
            t = repository.findTemplateByNameAndLanguage(templateKey, defaultLanguage);
        }
        if (t == null) {
            throw new EmailTemplateNotFoundException(templateKey);
        }
        String subject = t.getSubject();
        String content = substitutePlaceholders(t.getBody(), parameters);
        List<EmailTemplateRenderer> renderers = renderersByKey.getOrDefault(templateKey, Collections.emptyList());
        for (EmailTemplateRenderer r : renderers) {
            content = r.render(content, parameters);
        }
        return new RenderedEmail(subject, content);
    }

    private String substitutePlaceholders(String body, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) return body;
        String result = body;
        for (Map.Entry<String, Object> e : parameters.entrySet()) {
            String placeholder = "$$" + e.getKey() + "$$";
            String val = String.valueOf(e.getValue());
            result = result.replace(placeholder, val);
        }
        return result;
    }
}
