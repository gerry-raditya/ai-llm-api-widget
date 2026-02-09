package com.rumahhobi.dto;

import java.math.BigDecimal;
import java.util.List;

public class VisionEstimate {
    // === HASIL UTAMA ===
    public BigDecimal estimatedArea;      // m2
    public String areaUnit = "m2";

    // === METADATA ===
    public Integer confidence;             // 0 - 100 (%)
    public String method;                  // "visual-scale", "object-reference", dll

    // === ASUMSI & CATATAN ===
    public List<String> assumptions;       // asumsi yang dipakai AI
    public List<String> limitations;       // keterbatasan estimasi

    // === TRACEABILITY ===
    public boolean needConfirmation;       // apakah perlu konfirmasi user
    public String source = "VISION_AI";    // audit trail

    // === RAW RESPONSE (OPSIONAL, DEBUG) ===
    public String rawAiResponse;
}
