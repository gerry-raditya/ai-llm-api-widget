package com.rumahhobi.services;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IntentService {
    public String detect(String message) {

        if (message == null || message.isBlank()) return "UNKNOWN";

        String m = message.toLowerCase();

        if (m.contains("rumus")) return "RUMUS";
        if (m.contains("rab") && !m.matches(".*\\d+.*")) return "RUMUS_RAB";
        if (m.contains("rab") || m.contains("biaya")) return "RAB";
        if (m.contains("estimasi") || m.contains("berapa lama")) return "ESTIMASI";
        if (m.contains("simulasi")) return "SIMULASI";
        if (m.contains("alat") || m.contains("mesin")) return "WORKING_TOOL";
        if (m.contains("proses") || m.contains("produksi")) return "PRODUKSI";

        return "CHAT";
    }
}
