package com.rumahhobi.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class ToolsSchemaService {

    @Inject
    @Named("tools")
    DataSource toolsDataSource;

    public List<String> listTables() throws Exception {
        List<String> tables = new ArrayList<>();

        try (Connection connect = toolsDataSource.getConnection();
                PreparedStatement ps = connect.prepareStatement(
                        "SELECT table_name FROM information_schema.tables " +
                                "WHERE table_schema = DATABASE()");
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }

    public List<String> listColumns(String table) throws Exception {
        List<String> columns = new ArrayList<>();

        try (Connection connect = toolsDataSource.getConnection();
                PreparedStatement ps = connect.prepareStatement(
                        "SELECT column_name FROM information_schema.columns " +
                                "WHERE table_schema = DATABASE() AND table_name = ?")) {

            ps.setString(1, table);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString(1));
                }
            }
        }
        return columns;
    }

}
