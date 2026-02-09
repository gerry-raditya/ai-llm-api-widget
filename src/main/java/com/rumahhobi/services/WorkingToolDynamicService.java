package com.rumahhobi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class WorkingToolDynamicService {
    
    @Inject
    EntityManager em;

    public Map<String, Object> getWorkingToolsDynamic() {

        // 1. Ambil kolom yang tersedia
        List<String> columns = em.createNativeQuery("""
                    SELECT column_name
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'working_tool'
                    ORDER BY ordinal_position
                """).getResultList();

        if (columns.isEmpty()) {
            return Map.of("error", "Table working_tool not found");
        }

        // 2. Build SQL dinamis (AMAN)
        String selectCols = String.join(", ", columns);
        String sql = "SELECT " + selectCols + " FROM working_tool";

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        // 3. Convert ke JSON-friendly
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> record = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                record.put(columns.get(i), row[i]);
            }
            data.add(record);
        }

        return Map.of(
                "table", "working_tool",
                "columns", columns,
                "data", data);
    }

    public Map<String, Object> getProductionMetadata() {

        List<Object[]> meta = em.createNativeQuery("""
                    SELECT table_name, column_name, data_type
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name IN ('product', 'production_process')
                    ORDER BY table_name, ordinal_position
                """).getResultList();

        Map<String, List<Map<String, String>>> result = new HashMap<>();

        for (Object[] r : meta) {
            String table = (String) r[0];
            result.computeIfAbsent(table, k -> new ArrayList<>())
                    .add(Map.of(
                            "column", (String) r[1],
                            "type", (String) r[2]));
        }

        return Map.of("schema", result);
    }
}
