package com.example.app.dao;

import com.example.app.controllers.DBController;
import com.example.app.model.Utility;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CsvLogic implements DBQueries{
    private DBController dbController;

    private final String[] HEADERS = {
            "Building Name", "Water Usage", "Water Cost", "Electricity Usage", "Electricity Cost",
            "Sewage Cost", "Misc Usage", "Misc Cost", "Date (month)"
    };

    public CsvLogic(DBController dbController) {
        this.dbController = dbController;
    }

    public void exportCsvTemplate(File destinationFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile))) {
            writer.write(String.join(",", HEADERS));
            writer.newLine();

            Connection conn = dbController.getConnection();
            if (conn == null) return;

            String query = "SELECT name FROM building";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String buildingName = rs.getString("name");
                    writer.write(buildingName); // First column filled
                    writer.newLine(); // Empty rest of row
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void uploadUtilityCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int fetchBuildingID(String name, Connection conn) throws Exception {

    return 1;
    }

    private void insertUtilityRecord(Connection conn, Utility utility) throws Exception {
        String insert = "INSERT INTO utility (buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, usage_gal, misc_cost)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.executeUpdate();
        }
    }
}
