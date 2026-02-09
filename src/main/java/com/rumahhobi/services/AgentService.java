package com.rumahhobi.services;

import java.math.BigDecimal;

import com.rumahhobi.domain.SubscriptionPlan;
import com.rumahhobi.dto.VisionEstimate;
import com.rumahhobi.dto.oas.AiAgentDto;
import com.rumahhobi.entity.RabHistoryEntity;
import com.rumahhobi.provider.AiProvider;
import com.rumahhobi.provider.AiProviderResolver;
import com.rumahhobi.utils.AiUtils;
import com.rumahhobi.utils.Prompt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AgentService {

    @Inject
    RABService rabService;
    @Inject
    EstimationService estimationService;
    @Inject
    SimulationSafeService simulationService;
    @Inject
    DatasetService datasetService;
    @Inject
    IntentService intentService;
    @Inject
    ActivityLogService logService;
    @Inject
    AiProviderResolver aiProviderResolver;
    @Inject
    SubscriptionService subscriptionService;
    @Inject
    ConversationService conversationService;
    @Inject
    VisionService visionService;

    @Inject
    ToolsDatasetService toolsDatasetService;

    // ======================
    // ENTRY POINT
    // ======================
    public AiAgentDto.ResponseData process(AiAgentDto.Request req) {

        AiAgentDto.ResponseData res = AiUtils.baseResponse();
        String intent = intentService.detect(req.message);

        // 0. Vision
        if (AiUtils.hasImage(req)) {
            return handleVision(req, res);
        }

        // 1. RAB
        if ("RAB".equals(intent)) {
            return handleRab(req, res);
        }

        // 2. Estimasi
        if ("ESTIMASI".equals(intent)) {
            return handleEstimasi(req, res);
        }

        // 3. Simulasi
        if ("SIMULASI".equals(intent)) {
            return handleSimulasi(req, res);
        }

        // 4. Default
        return handleConceptual(req, res, intent);
    }

    // ======================
    // VISION
    // ======================
    private AiAgentDto.ResponseData handleVision(
            AiAgentDto.Request req,
            AiAgentDto.ResponseData res) {

        VisionEstimate estimate = visionService.estimateBuilding(req.image, req.clientId);

        String context = datasetService.buildContext(req.clientId, "VISION");

        res.reply = generateSafe(
                req.clientId,
                Prompt.VISION,
                context + "\nVISION_ESTIMATE:\n" + estimate, true);

        logService.log(req.clientId, req.userId, "VISION", req.message);
        return res;
    }

    // ======================
    // RAB
    // ======================
    private AiAgentDto.ResponseData handleRab(
            AiAgentDto.Request req,
            AiAgentDto.ResponseData res) {

        Long sessionId = conversationService
                .getOrCreateSession(req.clientId, req.userId, "RAB");

        var memory = conversationService.loadState(sessionId);

        BigDecimal material = AiUtils.resolveValue(
                req.materialCost, memory, "materialCost");
        BigDecimal labor = AiUtils.resolveValue(
                req.laborCost, memory, "laborCost");
        BigDecimal overhead = AiUtils.resolveValue(
                req.overheadCost, memory, "overheadCost");

        AiUtils.persistMemory(sessionId, req);

        if (material == null || labor == null || overhead == null) {
            return explainPartialRab(req, res);
        }

        return finalizeRab(
                req, res, sessionId, material, labor, overhead);
    }

    private AiAgentDto.ResponseData explainPartialRab(
        AiAgentDto.Request req,
        AiAgentDto.ResponseData res) {

    String baseDataset =
            datasetService.buildContext(req.clientId, "RAB");

    String toolsDataset =
            toolsDatasetService.loadDatasetForAi();

    String context = baseDataset
            + "\n\n"
            + toolsDataset;

    res.reply = generateSafe(
            req.clientId,
            Prompt.RAB_PARTIAL,
            context,
            true
    );

    logService.log(req.clientId, req.userId, "RAB_PARTIAL", req.message);
    return res;
}

    @Transactional
    AiAgentDto.ResponseData finalizeRab(
            AiAgentDto.Request req,
            AiAgentDto.ResponseData res,
            Long sessionId,
            BigDecimal material,
            BigDecimal labor,
            BigDecimal overhead) {

        BigDecimal total = rabService.hitungRAB(material, labor, overhead);

        RabHistoryEntity rab = new RabHistoryEntity();
        rab.clientId = req.clientId;
        rab.userId = req.userId;
        rab.materialCost = material;
        rab.laborCost = labor;
        rab.overheadCost = overhead;
        rab.totalCost = total;
        rab.persist();

        conversationService.closeSession(sessionId);

        res.reply = "Total RAB produksi adalah " + total;
        res.result = total.doubleValue();

        logService.log(req.clientId, req.userId, "RAB", req.message);
        return res;
    }

    // ======================
    // ESTIMASI
    // ======================
    private AiAgentDto.ResponseData handleEstimasi(
            AiAgentDto.Request req,
            AiAgentDto.ResponseData res) {

        Integer hari = estimationService.estimasiHari(
                req.quantity, req.dailyCapacity);

        res.reply = "Estimasi waktu produksi sekitar " + hari + " hari";

        logService.log(req.clientId, req.userId, "ESTIMASI", req.message);
        return res;
    }

    // ======================
    // SIMULASI
    // ======================
    private AiAgentDto.ResponseData handleSimulasi(
        AiAgentDto.Request req,
        AiAgentDto.ResponseData res) {

    String simulationData =
            simulationService.readLastSimulation().toString();

    String toolsDataset =
            toolsDatasetService.loadDatasetForAi();

    String context = simulationData
            + "\n\n"
            + toolsDataset;

    res.reply = generateSafe(
            req.clientId,
            Prompt.SIMULASI,
            context,
            true
    );

    logService.log(req.clientId, req.userId, "SIMULASI", req.message);
    return res;
}


    // ======================
    // DEFAULT / CONCEPTUAL
    // ======================
    private AiAgentDto.ResponseData handleConceptual(
            AiAgentDto.Request req,
            AiAgentDto.ResponseData res,
            String intent) {

        String baseDataset = datasetService.buildContext(req.clientId, intent);

        // 🔥 INI YANG BARU
        String toolsDataset = toolsDatasetService.loadDatasetForAi();

        String context = baseDataset
                + "\n\n"
                + toolsDataset;

        res.reply = generateSafe(
                req.clientId,
                Prompt.CONCEPTUAL + req.message,
                context,
                true);

        logService.log(req.clientId, req.userId, intent, req.message);
        return res;
    }

    // ======================
    // AI SAFE CALL (CORE FIX)
    // ======================
    private String generateSafe(
            String clientId,
            String prompt,
            String context,
            boolean useThinking) {

        SubscriptionPlan plan = subscriptionService.getPlan(clientId);

        AiProvider primary = aiProviderResolver.resolve(plan);

        try {
            return useThinking
                    ? primary.generateThink(prompt, context)
                    : primary.generate(prompt, context);

        } catch (Exception primaryError) {

            System.err.println(
                    "Primary AI failed (" + plan + "), fallback to Gemini");
            primaryError.printStackTrace();

            try {
                AiProvider gemini = aiProviderResolver.resolve(SubscriptionPlan.BASIC);

                if (useThinking) {
                    try {
                        return gemini.generateThink(prompt, context);
                    } catch (Exception thinkError) {
                        // 🔥 CRITICAL FIX
                        System.err.println(
                                "Gemini thinking overloaded, fallback to normal");
                        return gemini.generate(prompt, context);
                    }
                } else {
                    return gemini.generate(prompt, context);
                }

            } catch (Exception ex) {
                throw new RuntimeException(
                        "All AI providers failed", ex);
            }
        }
    }
}
