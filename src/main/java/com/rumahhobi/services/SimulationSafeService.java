package com.rumahhobi.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class SimulationSafeService {
    @Inject
    EntityManager em;

    public Map<String, Object> readLastSimulation() {

        // 1. Cek kolom
        List<String> columns = em.createNativeQuery("""
                    SELECT column_name
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'simulation_result'
                """).getResultList();

        if (columns.isEmpty()) {
            return Map.of("message", "No simulation_result table");
        }

        // 2. Ambil data terakhir
        List<Object[]> rows = em.createNativeQuery("""
                    SELECT * FROM simulation_result
                    ORDER BY id DESC
                    LIMIT 1
                """).getResultList();

        if (rows.isEmpty()) {
            return Map.of(
                    "columns", columns,
                    "data", "No simulation data available");
        }

        Object[] row = rows.get(0);
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            result.put(columns.get(i), row[i]);
        }

        return Map.of(
                "columns", columns,
                "data", result);
    }
}
