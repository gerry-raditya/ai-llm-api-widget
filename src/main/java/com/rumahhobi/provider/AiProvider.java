package com.rumahhobi.provider;

public interface AiProvider {
    String generate(String prompt, String context);

    default String generateThink(String prompt, String context) {
        // fallback ke normal generate
        return generate(prompt, context);
    }

    default String generateWithImage(String prompt, byte[] image) {
        throw new UnsupportedOperationException("Vision not supported");
    }
}
