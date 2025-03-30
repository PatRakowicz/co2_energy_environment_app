package com.example.app.dao;

import com.example.app.controllers.DBController;
import com.example.app.model.Building;
import com.example.app.model.Utility;

import java.io.*;
import java.sql.*;

public class CsvLogic implements DBQueries {
    private final DBController dbController;

    private final String[] HEADERS = {
            "Building Name", "Water Usage", "Water Cost", "Electricity Usage", "Electricity Cost",
            "Sewage Cost", "Misc Usage", "Misc Cost", "Date (YYYY-MM-DD)"
    };

    public CsvLogic(DBController dbController) {
        this.dbController = dbController;
    }


    public void importUtilityCSV(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Connection conn = dbController.getConnection();
            if (conn == null) {
                System.out.println("No active DB connection");
                return;
            }

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length < HEADERS.length) continue;

                String buildingName = parts[0].trim();
                if (buildingName.isEmpty()) continue;

                Building building = fetchBuildingByName(buildingName, conn);
                if (building == null) {
                    System.out.printf("Skipping unknown building: %s%n", buildingName);
                    continue;
                }

                Date date = parseDateOrNull(parts[8]);
                if (date == null) {
                    System.out.printf("Skipping row with missing/invalid date for building: %s%n", buildingName);
                    continue;
                }

                Utility utility = new Utility();
                utility.setBuildingID(building.getBuildingID());
                utility.setDate(date);
                utility.setWaterUsage(parseOrDefault(parts[1]));
                utility.setWaterCost(parseOrDefault(parts[2]));
                utility.setElectricityUsage(parseOrDefault(parts[3]));
                utility.setElectricityCost(parseOrDefault(parts[4]));
                utility.setSewageCost(parseOrDefault(parts[5]));
                utility.setUsageGal(parseOrDefault(parts[6])); // misc usage
                utility.setMiscCost(parseOrDefault(parts[7]));

                insertUtility(conn, building, utility);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportCsvTemplate(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.join(",", HEADERS));
            writer.newLine();

            Connection conn = dbController.getConnection();
            if (conn == null) return;

            String query = "SELECT name FROM building";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String buildingName = rs.getString("name");
                    writer.write(buildingName);
                    writer.newLine();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private float parseOrDefault(String value) {
        try {
            return value.trim().isEmpty() ? 0f : Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    private Date parseDateOrNull(String value) {
        try {
            return value.trim().isEmpty() ? null : java.sql.Date.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Building fetchBuildingByName(String name, Connection conn) throws SQLException {
        String query = "SELECT * FROM building WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Building building = new Building();
                building.setBuildingID(rs.getInt("buildingID"));
                building.setName(rs.getString("name"));
                return building;
            }
        }
        return null;
    }

    private void insertUtility(Connection conn, Building building, Utility utility) throws SQLException {
        String insert = "INSERT INTO utility (buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, usage_gal, misc_cost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, building.getBuildingID());
            stmt.setDate(2, new java.sql.Date(utility.getDate().getTime()));
            stmt.setFloat(3, utility.getElectricityUsage());
            stmt.setFloat(4, utility.getElectricityCost());
            stmt.setFloat(5, utility.getWaterUsage());
            stmt.setFloat(6, utility.getWaterCost());
            stmt.setFloat(7, utility.getSewageCost());
//            stmt.setFloat(8, utility.getUsageGal());
            stmt.setFloat(9, utility.getMiscCost());
            stmt.executeUpdate();
        }
    }
}
