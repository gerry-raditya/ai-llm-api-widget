package com.rumahhobi.services;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class DatasetService {
    @Inject
    EntityManager em;

    @Inject
    ToolsDatasetService toolsDatasetService;

    public String buildContext(String clientId, String intent) {

        StringBuilder ctx = new StringBuilder();

        ctx.append("INTENT: ").append(intent).append("\n\n");

        // Dataset utama (placeholder dulu)
        ctx.append("DATASET UTAMA:\n");
        ctx.append("Belum ada dataset tambahan.\n\n");

        // Dataset tools (REAL DATA)
        ctx.append(toolsDatasetService.buildDataset());

        return ctx.toString();
    }
    
    private String loadBaseDataset(String clientId, String intent) {

        // contoh sederhana (hardcode dulu)
        if ("RAB".equals(intent)) {
            return "ATURAN RAB:\n- Material + Labor + Overhead\n";
        }

        if ("SIMULASI".equals(intent)) {
            return "ATURAN SIMULASI:\n- Kapasitas alat mempengaruhi hasil\n";
        }

        return "DATASET UMUM PRODUKSI KONVEKSI\n";
    }

    /**
     * ==============================
     * DATASET: WORKING TOOLS (DB)
     * ==============================
     */
    private String buildWorkingToolDataset(String clientId) {

        // 1. Ambil kolom yang ADA
        List<String> columns = em.createNativeQuery("""
                    SELECT column_name
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'working_tool'
                    ORDER BY ordinal_position
                """).getResultList();

        if (columns.isEmpty()) {
            return "Dataset working tools tidak tersedia di database.";
        }

        // 2. Build SQL dinamis (AMAN)
        String sql = "SELECT " + String.join(", ", columns)
                + " FROM working_tool";

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        // 3. Build dataset JSON-like
        StringBuilder dataset = new StringBuilder();
        dataset.append("Dataset Working Tools:\n");

        for (Object[] row : rows) {
            dataset.append("- Tool:\n");
            for (int i = 0; i < columns.size(); i++) {
                dataset.append("  ")
                        .append(columns.get(i))
                        .append(": ")
                        .append(row[i])
                        .append("\n");
            }
        }

        if (rows.isEmpty()) {
            dataset.append("Tidak ada data alat produksi.\n");
        }

        return dataset.toString();
    }


    /**
     * ==============================
     * DATASET: PRODUKSI (BASIC)
     * ==============================
     */
    private String buildProductionDataset(String clientId) {

        List<Object[]> rows = em.createNativeQuery("""
                    SELECT p.name, pp.step_order, pp.step_name
                    FROM product p
                    JOIN production_process pp ON p.id = pp.product_id
                    ORDER BY p.name, pp.step_order
                """).getResultList();

        if (rows.isEmpty()) {
            return "Data proses produksi belum tersedia.";
        }

        StringBuilder dataset = new StringBuilder();
        dataset.append("Dataset Proses Produksi:\n");

        for (Object[] r : rows) {
            dataset.append("- Produk: ").append(r[0])
                    .append(", Step ").append(r[1])
                    .append(": ").append(r[2])
                    .append("\n");
        }

        return dataset.toString();
    }

    private String buildSimulationDataset(String clientId) {

        // 1. Ambil kolom simulation_result (schema-aware)
        List<String> columns = em.createNativeQuery("""
                    SELECT column_name
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'simulation_result'
                    ORDER BY ordinal_position
                """).getResultList();

        if (columns.isEmpty()) {
            return "Tabel simulation_result tidak tersedia di database.";
        }

        // 2. Ambil simulasi terakhir
        List<Object[]> rows = em.createNativeQuery("""
                    SELECT * FROM simulation_result
                    ORDER BY id DESC
                    LIMIT 1
                """).getResultList();

        if (rows.isEmpty()) {
            return "Belum ada data simulasi produksi.";
        }

        Object[] row = rows.get(0);

        // 3. Build dataset
        StringBuilder ds = new StringBuilder();
        ds.append("Dataset Hasil Simulasi Produksi (database):\n");

        for (int i = 0; i < columns.size(); i++) {
            ds.append("- ")
                    .append(columns.get(i))
                    .append(": ")
                    .append(row[i])
                    .append("\n");
        }

        return ds.toString();
    }
}
