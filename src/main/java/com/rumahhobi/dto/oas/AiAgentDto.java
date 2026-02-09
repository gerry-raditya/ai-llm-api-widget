package com.rumahhobi.dto.oas;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.rumahhobi.dto.VisionEstimate;


@Schema(
    name = "AiAgentDto",
    description = "Unified Request & Response DTO untuk AI Customer Agent"
)
public class AiAgentDto {

    public static class Request {

        @Schema(description = "Perintah atau pesan user", example = "Hitung RAB 100 kaos")
        public String message;

        @Schema(description = "Client / tenant ID", example = "rumahhobi")
        public String clientId;

        @Schema(description = "User ID", example = "user-001")
        public String userId;

        @Schema(description = "Jumlah produksi", example = "100")
        public Integer quantity;

        @Schema(description = "Biaya bahan")
        public Double materialCost;

        @Schema(description = "Biaya tenaga kerja")
        public Double laborCost;

        @Schema(description = "Biaya overhead")
        public Double overheadCost;

        @Schema(description = "Kapasitas produksi per hari")
        public Integer dailyCapacity;
        @Schema(description = "Subscription Plan", examples = "FREE, BASIC, PREMIUM")
        public String subscriptionPlan;

        // Vision
        @Schema(
            description = "Gambar (base64) untuk analisa visual",
            examples = "iVBORw0KGgoAAAANSUhEUgAA..."
        )
        public String image;
    }

    public static class ResponseData {

        @Schema(description = "Jawaban AI")
        public String reply;

        @Schema(description = "Hasil perhitungan")
        public Double result;

        @Schema(description = "Detail hasil")
        public String detail;

        @Schema(description = "Timestamp response")
        public Long timestamp;
        @Schema(description = "Apakah AI menunggu input lanjutan")
        public Boolean waitingInput;
        @Schema(description = "Field apa saja yang dibutuhkan")
        public List<String> requiredFields;

        public VisionEstimate visionEstimate;
        public Boolean waitingConfirmation;
         // Audit / UX helper
        public String resultSource; // DB | VISION_AI | USER_INPUT
    }
}
