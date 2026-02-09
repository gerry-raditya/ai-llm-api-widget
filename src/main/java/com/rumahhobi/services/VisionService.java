package com.rumahhobi.services;

import java.util.Base64;

import com.rumahhobi.domain.SubscriptionPlan;
import com.rumahhobi.dto.VisionEstimate;
import com.rumahhobi.mapper.VisionEstimateMapper;
import com.rumahhobi.provider.AiProvider;
import com.rumahhobi.provider.AiProviderResolver;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VisionService {

    @Inject
    AiProviderResolver resolver;

    public VisionEstimate estimateBuilding(
            String imageBase64,
            String clientId
    ) {
        if (imageBase64 == null || imageBase64.isBlank()) {
            throw new IllegalArgumentException("Image is required for vision analysis");
        }

        byte[] imageBytes = decodeBase64(imageBase64);

        AiProvider ai = resolver.resolve(
                SubscriptionPlan.BASIC // Gemini (Vision)
        );

        String prompt = """
                Dari gambar bangunan berikut:
                - Perkirakan luas bangunan (m2)
                - Sebutkan asumsi yang digunakan
                - Sebutkan keterbatasan
                - Berikan confidence level (0-100)
                Jawab dalam format JSON.
                """;

        String response = ai.generateWithImage(prompt, imageBytes);

        return VisionEstimateMapper.from(response);
    }

    private byte[] decodeBase64(String base64) {
        try {
            // support: data:image/jpeg;base64,...
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            return Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid base64 image format", e);
        }
    }
}
