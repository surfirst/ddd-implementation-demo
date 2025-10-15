package com.example.enrollment.infrastructure.adapters.emailrenderers;

import com.example.enrollment.domain.emailtemplates.EmailTemplate;
import com.example.enrollment.domain.emailtemplates.EmailTemplateManager;
import com.example.enrollment.domain.emailtemplates.EmailTemplateRepository;
import com.example.enrollment.domain.shared.SupportedLanguage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CompositeRendererTest {

    @Test
    void welcomeTemplate_Should_BeProcessed_By_QRCode_And_Save2Photo_Renderers() {
        final String templateKey = "welcome";
        final int cardLength = 16;
        final String playerId = "10000086";
        final String rank = "A";
        final String playerName = "Tom";

        String templateBody = "__$$player_card$$__$$card_query$$";
        EmailTemplate template = new EmailTemplate(templateKey, "subject", templateBody, SupportedLanguage.EN);

        EmailTemplateRepository repo = Mockito.mock(EmailTemplateRepository.class);
        when(repo.findTemplateByNameAndLanguage(any(), Mockito.eq(SupportedLanguage.EN))).thenReturn(template);

        EmailTemplateManager manager = new EmailTemplateManager(repo, SupportedLanguage.EN);
        manager.addRenderer(templateKey, new QRCodeRenderer(cardLength));
        manager.addRenderer(templateKey, new Save2PhotoRender());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("player_id", playerId);
        parameters.put("rank", rank);
        parameters.put("player_name", playerName);

        var rendered = manager.render(templateKey, parameters, SupportedLanguage.EN);
        String content = rendered.content();

        String expectedCardId = ";" + leftPad(playerId, cardLength) + "?";
        String expectedCardEncoded = URLEncoder.encode(expectedCardId, StandardCharsets.UTF_8);
        assertTrue(content.contains(expectedCardEncoded));

        String expectedJson = "{\"rank\":\"" + rank + "\",\"player_name\":\"" + playerName + "\",\"player_id\":\"" + playerId + "\"}";
        String expectedJsonBase64 = Base64.getEncoder().encodeToString(expectedJson.getBytes(StandardCharsets.UTF_8));
        assertTrue(content.endsWith(expectedJsonBase64));

        assertTrue(content.startsWith("__" + expectedCardEncoded + "__"));
    }

    private static String leftPad(String s, int len) {
        if (s.length() >= len) return s;
        return "0".repeat(len - s.length()) + s;
    }
}
