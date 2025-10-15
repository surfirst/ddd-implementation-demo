package com.example.enrollment.infrastructure.adapters.emailrenderers;

import com.example.enrollment.domain.emailtemplates.EmailTemplateRenderer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class Save2PhotoRender implements EmailTemplateRenderer {
    @Override
    public String render(String body, Map<String, Object> parameters) {
        String rank = stringOf(parameters.get("rank"));
        String playerName = stringOf(parameters.get("player_name"));
        String playerId = stringOf(parameters.get("player_id"));
        String json = "{\"rank\":\"" + rank + "\",\"player_name\":\"" + playerName + "\",\"player_id\":\"" + playerId + "\"}";
        String b64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        return body.replace("$$card_query$$", b64);
    }

    private String stringOf(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
