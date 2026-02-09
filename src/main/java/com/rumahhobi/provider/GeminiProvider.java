package com.rumahhobi.provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeminiProvider implements AiProvider {
    private final Client client;

    @Inject
    public GeminiProvider(
        @ConfigProperty(name = "gemini.api-key") String apiKey
    ) {
        this.client = Client.builder()
            .apiKey(apiKey)
            .build();
    }

    private static final String NORMAL_MODEL = "models/gemini-1.5-flash";
    private static final String THINK_MODEL = "models/gemini-1.5-pro";
    // alternatif: "gemini-2.0-flash-thinking"

    @Override
    public String generate(String prompt, String context) {
        try {
            GenerateContentResponse response =
                client.models.generateContent(
                    NORMAL_MODEL,
                    context + "\nUser: " + prompt,
                    null
                );

            return response.text();

        } catch (Exception e) {
            throw new RuntimeException("Gemini generate error", e);
        }
    }

    @Override
    public String generateThink(String prompt, String context) {
        try {
            GenerateContentResponse response =
                client.models.generateContent(
                    THINK_MODEL,
                    context + "\nUser: " + prompt,
                    null
                );

            return response.text();

        } catch (Exception e) {
            throw new RuntimeException("Gemini think error", e);
        }
    }
}
