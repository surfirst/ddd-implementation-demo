package com.example.enrollment.infrastructure.adapters.emailrenderers;

import com.example.enrollment.domain.emailtemplates.EmailTemplateRenderer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QRCodeRenderer implements EmailTemplateRenderer {
    private final int cardLength;

    public QRCodeRenderer(int cardLength) {
        this.cardLength = cardLength;
    }

    @Override
    public String render(String body, Map<String, Object> parameters) {
        Object pid = parameters.get("player_id");
        if (pid == null) return body;
        String playerId = String.valueOf(pid);
        String padded = leftPad(playerId, cardLength);
        String cardId = ";" + padded + "?";
        String encoded = URLEncoder.encode(cardId, StandardCharsets.UTF_8);
        return body.replace("$$player_card$$", encoded);
    }

    private static String leftPad(String s, int len) {
        if (s.length() >= len) return s;
        return "0".repeat(len - s.length()) + s;
    }
}
