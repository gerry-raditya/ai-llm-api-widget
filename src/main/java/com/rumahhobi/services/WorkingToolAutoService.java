package com.rumahhobi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class WorkingToolAutoService {
    @Inject
    EntityManager em;

    public Map<String, Object> readAllWorkingTools() {

        // 1. Ambil semua kolom
        List<String> columns = em.createNativeQuery("""
                    SELECT column_name
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'working_tool'
                    ORDER BY ordinal_position
                """).getResultList();

        if (columns.isEmpty()) {
            return Map.of("message", "Table working_tool not found");
        }

        // 2. Build SQL aman
        String sql = "SELECT " + String.join(", ", columns) + " FROM working_tool";
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        // 3. Convert ke JSON
        List<Map<String, Object>> tools = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> rec = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                rec.put(columns.get(i), row[i]);
            }
            tools.add(rec);
        }

        return Map.of(
                "table", "working_tool",
                "columns", columns,
                "data", tools);
    }
}
