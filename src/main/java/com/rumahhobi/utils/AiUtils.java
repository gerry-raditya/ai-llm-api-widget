package com.rumahhobi.utils;

import java.math.BigDecimal;
import java.util.Map;

import com.rumahhobi.dto.oas.AiAgentDto;
import com.rumahhobi.services.ConversationService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AiUtils {

    private final static ConversationService  conversationService = new ConversationService();
    
    public static AiAgentDto.ResponseData baseResponse() {
        AiAgentDto.ResponseData res = new AiAgentDto.ResponseData();
        res.timestamp = System.currentTimeMillis();
        return res;
    }

    public static boolean hasImage(AiAgentDto.Request req) {
        return req.image != null && !req.image.isBlank();
    }

    public static BigDecimal resolveValue(
            Double input, Map<String, String> memory, String key) {

        if (input != null)
            return BigDecimal.valueOf(input);
        if (memory.containsKey(key))
            return new BigDecimal(memory.get(key));
        return null;
    }

    public static void persistMemory(Long sessionId, AiAgentDto.Request req) {
        if (req.materialCost != null)
            conversationService.saveField(
                    sessionId, "materialCost", req.materialCost.toString());
        if (req.laborCost != null)
            conversationService.saveField(
                    sessionId, "laborCost", req.laborCost.toString());
        if (req.overheadCost != null)
            conversationService.saveField(
                    sessionId, "overheadCost", req.overheadCost.toString());
    }

}
