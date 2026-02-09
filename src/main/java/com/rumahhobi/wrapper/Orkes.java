package com.rumahhobi.wrapper;

import java.util.Map;

import com.rumahhobi.services.ProductionQaDynamicService;
import com.rumahhobi.services.SimulationSafeService;
import com.rumahhobi.services.WorkingToolAutoService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Orkes {
    @Inject
    ProductionQaDynamicService productionQaDynamicService;
    @Inject
    WorkingToolAutoService workingToolAutoService;
    @Inject
    SimulationSafeService simulationSafeService;
    
    public Map<String, Object> handle(String intent) {
        return switch (intent) {
            case "QA_PRODUKSI" -> productionQaDynamicService.readProductionData();
            case "WORKING_TOOL" -> workingToolAutoService.readAllWorkingTools();
            case "SIMULASI" -> simulationSafeService.readLastSimulation();
            default -> Map.of("message", "Intent not supported");
        };
    }
}
