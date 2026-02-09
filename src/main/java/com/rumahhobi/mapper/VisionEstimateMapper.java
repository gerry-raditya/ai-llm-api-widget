package com.rumahhobi.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumahhobi.dto.VisionEstimate;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VisionEstimateMapper {
    public static VisionEstimate from(String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(aiResponse, VisionEstimate.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid vision AI response", e);
        }
    }
}
