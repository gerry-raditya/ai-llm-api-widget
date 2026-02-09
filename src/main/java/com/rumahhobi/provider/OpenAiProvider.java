package com.rumahhobi.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OpenAiProvider implements AiProvider {

    @ConfigProperty(name = "openai.api-key")
    String apiKey;

    @ConfigProperty(name = "openai.model", defaultValue = "gpt-4o-mini")
    String model;

    @Inject
    ObjectMapper mapper;

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String generate(String prompt, String context) {
        try {
            var payload = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", context),
                    Map.of("role", "user", "content", prompt)
                )
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    mapper.writeValueAsString(payload)
                ))
                .build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode json = mapper.readTree(response.body());
            return json.get("choices").get(0)
                .get("message").get("content").asText();

        } catch (Exception e) {
            throw new RuntimeException("OpenAI error", e);
        }
    }
}
