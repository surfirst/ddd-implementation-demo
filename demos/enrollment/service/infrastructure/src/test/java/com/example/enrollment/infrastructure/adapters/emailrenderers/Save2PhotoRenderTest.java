package com.example.enrollment.infrastructure.adapters.emailrenderers;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Save2PhotoRenderTest {

    @Test
    void shouldReplacePlaceholderWithBase64Json() {
        String template = "Hello $$card_query$$ End";
        Map<String, Object> paras = new HashMap<>();
        paras.put("rank", "0");
        paras.put("player_name", "Mike");
        paras.put("player_id", "1000086");

        Save2PhotoRender renderer = new Save2PhotoRender();

        String json = "{\"rank\":\"0\",\"player_name\":\"Mike\",\"player_id\":\"1000086\"}";
        String expectedEncoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String expected = "Hello " + expectedEncoded + " End";

        String result = renderer.render(template, paras);
        assertEquals(expected, result);
    }
}
