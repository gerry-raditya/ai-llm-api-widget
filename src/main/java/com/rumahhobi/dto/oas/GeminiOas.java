package com.rumahhobi.dto.oas;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Gemini", description = "DTO OpenAPI untuk Google Gemini generateContent")
public class GeminiOas {
    public static class Request {
        public List<Content> contents;
        public GenerationConfig generationConfig;
    }

    public static class GenerationConfig {
        public ThinkingConfig thinkingConfig;
    }

    public static class ThinkingConfig {
        public String thinkingLevel; // low | medium | high
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }

    public static class Response {
        public List<Candidate> candidates;
    }

    public static class Candidate {
        public Content content;
    }
}
