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
import java.util.Map;

@ApplicationScoped
public class OllamaProvider implements AiProvider {

    @ConfigProperty(name = "ollama.base-url")
    String baseUrl;

    @ConfigProperty(name = "ollama.model", defaultValue = "qwen2.5:7b-instruct")
    String model;

    @Inject
    ObjectMapper mapper;

    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String generate(String prompt, String context) {
        try {
            var payload = Map.of(
                "model", model,
                "prompt", context + "\nUser: " + prompt,
                "stream", false
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    mapper.writeValueAsString(payload)
                ))
                .build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama HTTP " + response.statusCode() + ": " + response.body());
            }

            JsonNode json = mapper.readTree(response.body());

            if (json.has("error")) {
                throw new RuntimeException("Ollama error: " + json.get("error").asText());
            }

            return json.path("response").asText();

        } catch (Exception e) {
            throw new RuntimeException("Ollama error", e);
        }
    }
}
