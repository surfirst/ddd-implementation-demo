package com.example.enrollment.domain.emailtemplates;

import java.util.Map;

public interface EmailTemplateRenderer {
    String render(String body, Map<String, Object> parameters);
}
