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

    // private static final String NORMAL_MODEL = "models/gemini-3-flash-preview";
    // private static final String THINK_MODEL = "models/gemini-1.5-pro";

    @Override
    public String generate(String prompt, String context) {
        return call("gemini-3-flash-preview", prompt, context, false);
    }

    @Override
    public String generateThink(String prompt, String context) {
        return call("gemini-1.5-pro", prompt, context, true);
    }

    private String call(String model, String prompt, String context, boolean thinking) {

        GeminiOas.Request req = new GeminiOas.Request();

        GeminiOas.Part p = new GeminiOas.Part();
        p.text = Prompt.LANG_ID + "\n\n" + context + "\nUser: " + prompt;

        GeminiOas.Content c = new GeminiOas.Content();
        c.parts = List.of(p);
        req.contents = List.of(c);

        // ⚠️ HANYA kirim generationConfig jika perlu
        if (thinking) {
            GeminiOas.GenerationConfig gc = new GeminiOas.GenerationConfig();
            GeminiOas.ThinkingConfig tc = new GeminiOas.ThinkingConfig();
            tc.thinkingLevel = "low"; // jangan high dulu
            gc.thinkingConfig = tc;
            req.generationConfig = gc;
        }

        GeminiOas.Response res = client.generateContent("models/" + model, req, apiKey);

        return res.candidates
                .get(0).content.parts
                .get(0).text;
    }
}
