package com.rumahhobi.provider;

import com.rumahhobi.domain.SubscriptionPlan;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AiProviderResolver {

    @Inject 
    OllamaProvider ollama;
    @Inject 
    GeminiProvider gemini;
    @Inject 
    OpenAiProvider openai;

    public AiProvider resolve(SubscriptionPlan plan) {
        return switch (plan) {
            case FREE -> ollama;
            case BASIC -> gemini;
            case PRO -> openai;
        };
    }
}
