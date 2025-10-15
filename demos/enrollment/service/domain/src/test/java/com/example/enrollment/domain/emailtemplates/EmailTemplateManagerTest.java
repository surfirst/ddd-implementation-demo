package com.example.enrollment.domain.emailtemplates;

import com.example.enrollment.domain.shared.SupportedLanguage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EmailTemplateManagerTest {

    @Test
    void renderShouldBeCalledWhenRendering() {
        EmailTemplateRepository repo = Mockito.mock(EmailTemplateRepository.class);
        EmailTemplate template = new EmailTemplate("someName", "subject", "__$$name$$__", SupportedLanguage.EN);
        when(repo.findTemplateByNameAndLanguage(any(), Mockito.eq(SupportedLanguage.EN))).thenReturn(template);

        EmailTemplateManager manager = new EmailTemplateManager(repo, SupportedLanguage.EN);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Tom");

        RenderedEmail rendered = manager.render("someName", params, SupportedLanguage.EN);
        assertEquals("subject", rendered.subject());
        assertEquals("__Tom__", rendered.content());
    }

    @Test
    void templateNotFoundExceptionShouldBeThrownWhenTemplateNotFound() {
        EmailTemplateRepository repo = Mockito.mock(EmailTemplateRepository.class);
        when(repo.findTemplateByNameAndLanguage(any(), Mockito.eq(SupportedLanguage.EN))).thenReturn(null);
        EmailTemplateManager manager = new EmailTemplateManager(repo, SupportedLanguage.EN);

        assertThrows(EmailTemplateNotFoundException.class,
                () -> manager.render("someTemplate", new HashMap<>(), SupportedLanguage.EN));
    }

    @Test
    void renderShouldBeUsedIfFound_withCustomRenderer() {
        EmailTemplateRepository repo = Mockito.mock(EmailTemplateRepository.class);
        EmailTemplate template = new EmailTemplate("someName", "subject", "__$$name$$__$$player_card$$", SupportedLanguage.EN);
        when(repo.findTemplateByNameAndLanguage(any(), Mockito.eq(SupportedLanguage.EN))).thenReturn(template);

        EmailTemplateManager manager = new EmailTemplateManager(repo, SupportedLanguage.EN);
        manager.addRenderer("welcome", new TestQRCodeRenderer(16));

        Map<String, Object> params = new HashMap<>();
        params.put("name", "Tom");
        params.put("player_id", "10000086");

        RenderedEmail rendered = manager.render("welcome", params, SupportedLanguage.EN);
        String cardId = URLEncoder.encode(";0000000010000086?", StandardCharsets.UTF_8);
        assertEquals("subject", rendered.subject());
        assertEquals("__Tom__" + cardId, rendered.content());
    }

    @Test
    void emailTemplateShouldBeSelectedBasedOnLanguage() {
        EmailTemplateRepository repo = Mockito.mock(EmailTemplateRepository.class);
        EmailTemplate ten = new EmailTemplate("name", "subject", "English", SupportedLanguage.EN);
        EmailTemplate tcn = new EmailTemplate("name", "主题", "中文", SupportedLanguage.ZH);
        when(repo.findTemplateByNameAndLanguage("name", SupportedLanguage.EN)).thenReturn(ten);
        when(repo.findTemplateByNameAndLanguage("name", SupportedLanguage.ZH)).thenReturn(tcn);

        EmailTemplateManager manager = new EmailTemplateManager(repo, SupportedLanguage.EN);
        Map<String, Object> params = Map.of("name", "Tom");

        RenderedEmail en = manager.render("name", params, SupportedLanguage.EN);
        RenderedEmail cn = manager.render("name", params, SupportedLanguage.ZH);

        assertEquals("subject", en.subject());
        assertEquals("English", en.content());
        assertEquals("主题", cn.subject());
        assertEquals("中文", cn.content());
    }

    @Test
    void defaultLangShouldBeUsedIfPreferredNotFound() {
        EmailTemplateRepository repo = Mockito.mock(EmailTemplateRepository.class);
        EmailTemplate ten = new EmailTemplate("name", "subject", "English", SupportedLanguage.EN);
        when(repo.findTemplateByNameAndLanguage("name", SupportedLanguage.EN)).thenReturn(ten);
        when(repo.findTemplateByNameAndLanguage("name", SupportedLanguage.ZH)).thenReturn(null);

        EmailTemplateManager manager = new EmailTemplateManager(repo, SupportedLanguage.EN);
        Map<String, Object> params = Map.of("name", "Tom");

        RenderedEmail en = manager.render("name", params, SupportedLanguage.EN);
        RenderedEmail cn = manager.render("name", params, SupportedLanguage.ZH);

        assertEquals("subject", en.subject());
        assertEquals("English", en.content());
        assertEquals("subject", cn.subject());
        assertEquals("English", cn.content());
    }

    static class TestQRCodeRenderer implements EmailTemplateRenderer {
        private final int length;
        TestQRCodeRenderer(int length) { this.length = length; }
        @Override
        public String render(String body, Map<String, Object> parameters) {
            String playerId = String.valueOf(parameters.get("player_id"));
            String cardId = ";" + leftPad(playerId, length) + "?";
            String encoded = URLEncoder.encode(cardId, StandardCharsets.UTF_8);
            return body.replace("$$player_card$$", encoded);
        }
        private String leftPad(String s, int len) {
            if (s.length() >= len) return s;
            return "0".repeat(len - s.length()) + s;
        }
    }
}
