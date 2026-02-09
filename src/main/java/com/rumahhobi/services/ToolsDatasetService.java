package com.rumahhobi.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Set;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ToolsDatasetService {

    @Inject
    @DataSource("tools")
    AgroalDataSource toolsDs;

    // ===== CONFIG (WAJIB JELAS) =====
    private static final int SAMPLE_ROWS = 5;

    private static final Set<String> EXCLUDED_TABLES = Set.of(
            "flyway_schema_history",
            "audit_log",
            "user_token",
            "password_reset");

    private static final Set<String> EXCLUDED_COLUMNS = Set.of(
            "password",
            "token",
            "secret",
            "api_key");

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public String loadDatasetForAi() {

        StringBuilder sb = new StringBuilder();
        sb.append("""
                === WORKING TOOLS DATABASE DATASET ===
                Tujuan: Referensi struktur & data contoh untuk AI.
                Gunakan untuk menjawab pertanyaan produksi & simulasi.
                ====================================
                """);

        try (Connection conn = toolsDs.getConnection()) {

            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet tables = meta.getTables(
                    conn.getCatalog(), null, "%", new String[] { "TABLE" })) {

                while (tables.next()) {

                    String table = tables.getString("TABLE_NAME");

                    if (EXCLUDED_TABLES.contains(table.toLowerCase())) {
                        continue;
                    }

                    sb.append("\nTABLE: ").append(table).append("\n");

                    // ===== COLUMNS =====
                    sb.append("COLUMNS:\n");

                    try (ResultSet cols = meta.getColumns(
                            conn.getCatalog(), null, table, "%")) {

                        while (cols.next()) {
                            String col = cols.getString("COLUMN_NAME");

                            if (isSensitive(col))
                                continue;

                            sb.append("- ")
                                    .append(col)
                                    .append(" (")
                                    .append(cols.getString("TYPE_NAME"))
                                    .append(")\n");
                        }
                    }

                    // ===== SAMPLE DATA =====
                    sb.append("SAMPLE DATA:\n");

                    try (Statement st = conn.createStatement();
                            ResultSet rs = st.executeQuery(
                                    "SELECT * FROM `" + table + "` LIMIT " + SAMPLE_ROWS)) {

                        ResultSetMetaData rsmd = rs.getMetaData();

                        while (rs.next()) {
                            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                                String col = rsmd.getColumnName(i);

                                if (isSensitive(col))
                                    continue;

                                sb.append(col)
                                        .append("=")
                                        .append(rs.getString(i))
                                        .append(" ");
                            }
                            sb.append("\n");
                        }
                    }
                }
            }

        } catch (Exception e) {
            sb.append("\n[ERROR LOADING WORKING TOOLS DATASET]");
            e.printStackTrace();
        }

        return sb.toString();
    }

    private boolean isSensitive(String columnName) {
        return EXCLUDED_COLUMNS.contains(columnName.toLowerCase());
    }

    public String buildDataset() {

        StringBuilder dataset = new StringBuilder();
        dataset.append("DATASET DATABASE WORKING TOOLS:\n");

        try (Connection conn = toolsDs.getConnection()) {

            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet tables = meta.getTables(conn.getCatalog(), null, "%", new String[] { "TABLE" })) {

                while (tables.next()) {

                    String tableName = tables.getString("TABLE_NAME");

                    dataset.append("\nTABLE: ").append(tableName).append("\n");

                    try (Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 20")) {

                        ResultSetMetaData rsmd = rs.getMetaData();
                        int colCount = rsmd.getColumnCount();

                        while (rs.next()) {
                            for (int i = 1; i <= colCount; i++) {
                                dataset.append(rsmd.getColumnName(i))
                                        .append("=")
                                        .append(rs.getString(i))
                                        .append(" | ");
                            }
                            dataset.append("\n");
                        }
                    }
                }
            }

        } catch (Exception e) {
            dataset.append("\n[ERROR READING WORKING TOOLS DATABASE]");
        }

        return dataset.toString();
    }

    public String loadAllAsText() {
        StringBuilder sb = new StringBuilder();

        try (Connection c = toolsDs.getConnection()) {
            DatabaseMetaData meta = c.getMetaData();

            ResultSet tables = meta.getTables(null, null, "%", new String[] { "TABLE" });

            while (tables.next()) {
                String table = tables.getString("TABLE_NAME");
                sb.append("\nTABLE: ").append(table).append("\n");

                ResultSet cols = meta.getColumns(null, null, table, "%");

                while (cols.next()) {
                    sb.append("- ")
                            .append(cols.getString("COLUMN_NAME"))
                            .append(" (")
                            .append(cols.getString("TYPE_NAME"))
                            .append(")\n");
                }

                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM " + table + " LIMIT 20");

                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        sb.append(rsmd.getColumnName(i))
                                .append("=")
                                .append(rs.getString(i))
                                .append(" ");
                    }
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            return "TOOLS DATASET ERROR";
        }

        return sb.toString();
    }
}
