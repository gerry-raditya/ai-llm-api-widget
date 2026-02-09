package com.rumahhobi.provider;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.rumahhobi.clients.GeminiRestClient;
import com.rumahhobi.dto.oas.GeminiOas;
import com.rumahhobi.utils.Prompt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeminiProvider implements AiProvider {

    @Inject
    @RestClient
    GeminiRestClient client;

    @ConfigProperty(name = "gemini.api-key")
    String apiKey;

    private static final String NORMAL_MODEL = "models/gemini-3-flash-preview";
    private static final String THINK_MODEL  = "models/gemini-1.5-pro";

    @Override
    public String generate(String prompt, String context) {
        return call(NORMAL_MODEL, prompt, context, "low");
    }

    @Override
    public String generateThink(String prompt, String context) {
        return call(THINK_MODEL, prompt, context, "high");
    }

    private String call(String model, String prompt, String context, String thinking) {

        // ======================
        // SYSTEM + CONTEXT
        // ======================
        String fullPrompt =
            Prompt.BASE_SYSTEM + "\n\n" +
            (context != null ? context : "") +
            "\n\nPertanyaan user:\n" + prompt;

        // ======================
        // CONTENT
        // ======================
        GeminiOas.Part part = new GeminiOas.Part();
        part.text = fullPrompt;

        GeminiOas.Content content = new GeminiOas.Content();
        content.parts = List.of(part);

        GeminiOas.Request req = new GeminiOas.Request();
        req.contents = List.of(content);

        // ======================
        // THINKING CONFIG
        // ======================
        GeminiOas.ThinkingConfig tc = new GeminiOas.ThinkingConfig();
        tc.thinkingLevel = thinking;

        GeminiOas.GenerationConfig gc = new GeminiOas.GenerationConfig();
        gc.thinkingConfig = tc;

        req.generationConfig = gc;

        // ======================
        // CALL
        // ======================
        GeminiOas.Response res =
            client.generateContent(model, req, apiKey);

        if (res == null
            || res.candidates == null
            || res.candidates.isEmpty()
            || res.candidates.get(0).content == null
            || res.candidates.get(0).content.parts == null
            || res.candidates.get(0).content.parts.isEmpty()) {
            throw new RuntimeException("Gemini response kosong / tidak valid");
        }

        return res.candidates.get(0).content.parts.get(0).text;
    }
}
