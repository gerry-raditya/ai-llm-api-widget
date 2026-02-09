package com.rumahhobi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ProductionQaDynamicService {
    @Inject
    EntityManager em;

    public Map<String, Object> readProductionData() {

        // 1. Ambil schema
        List<Object[]> schemaRows = em.createNativeQuery("""
                    SELECT table_name, column_name
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name IN ('product', 'production_process')
                    ORDER BY table_name, ordinal_position
                """).getResultList();

        Map<String, List<String>> schema = new HashMap<>();
        for (Object[] r : schemaRows) {
            schema.computeIfAbsent((String) r[0], k -> new ArrayList<>())
                    .add((String) r[1]);
        }

        // 2. Ambil data aktual
        List<Object[]> dataRows = em.createNativeQuery("""
                    SELECT p.name, pp.step_order, pp.step_name
                    FROM product p
                    JOIN production_process pp ON p.id = pp.product_id
                    ORDER BY p.name, pp.step_order
                """).getResultList();

        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] r : dataRows) {
            data.add(Map.of(
                    "product", r[0],
                    "stepOrder", r[1],
                    "stepName", r[2]));
        }

        return Map.of(
                "schema", schema,
                "data", data);
    }
}
