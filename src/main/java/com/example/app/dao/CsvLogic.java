package com.example.app.dao;

import com.example.app.model.Building;
import com.example.app.model.Utility;

import java.io.*;
import java.sql.*;

public class CsvLogic implements DBQueries {
    private DBConn dbConn;

    private final String[] HEADERS = {
            "Building Name", "Water Usage", "Water Cost", "Electricity Usage", "Electricity Cost",
            "Sewage Cost", "Misc Cost"
    };

    public CsvLogic(DBConn dbConn) {
        this.dbConn = dbConn;
    }


    public void importUtilityCSV(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("No active DB connection");
                return;
            }

            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("Empty file.");
                return;
            }

            String[] headers = headerLine.split(",", -1);
            String sharedDateString = headers[headers.length - 1].trim();
            Date sharedDate = parseDateOrNull(sharedDateString);
            if (sharedDate == null) {
                System.out.println("Invalid or missing shared date in header: " + sharedDateString);
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                if (parts.length < 8) {
                    System.out.printf("Skipping row with too few columns: %s%n", line);
                    continue;
                }

                String buildingName = parts[0];
                if (buildingName.isEmpty()) continue;

                Building building = fetchBuildingByName(buildingName, conn);
                if (building == null) {
                    System.out.printf("Skipping unknown building: %s%n", buildingName);
                    continue;
                }

                Utility utility = new Utility();
                utility.setDate(sharedDate);
                utility.setBuildingID(building.getBuildingID());
                utility.setWaterUsage(parseOrDefault(parts[1]));
                utility.setWaterCost(parseOrDefault(parts[2]));
                utility.setElectricityUsage(parseOrDefault(parts[3]));
                utility.setElectricityCost(parseOrDefault(parts[4]));
                utility.setSewageCost(parseOrDefault(parts[5]));
                utility.setUsageGal(parseOrDefault(parts[6]));
                utility.setMiscCost(parseOrDefault(parts[7]));

                insertUtility(conn, building, utility);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportCsvTemplate(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("No active DB connection");
                return;
            }

            String sharedDatePlaceholder = "DD/MM/YYYY";
            writer.write(String.join(",", new String[]{
                    "Building Name", "Water Usage", "Water Cost", "Electricity Usage", "Electricity Cost",
                    "Sewage Cost", "Misc Cost", sharedDatePlaceholder
            }));
            writer.newLine();

            String query = "SELECT name FROM building";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String buildingName = rs.getString("name");
                    writer.write(buildingName + ",,,,,,");
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
            if (value.trim().isEmpty()) return null;

            String[] parts = value.trim().split("/");
            if (parts.length != 3) return null;

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day);
            return new java.sql.Date(cal.getTimeInMillis());
        } catch (Exception e) {
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
        String insert = "INSERT INTO utility (buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, misc_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, building.getBuildingID());
            stmt.setDate(2, new java.sql.Date(utility.getDate().getTime()));
            stmt.setFloat(3, utility.getElectricityUsage());
            stmt.setFloat(4, utility.getElectricityCost());
            stmt.setFloat(5, utility.getWaterUsage());
            stmt.setFloat(6, utility.getWaterCost());
            stmt.setFloat(7, utility.getSewageCost());
            stmt.setFloat(8, utility.getMiscCost());

            stmt.executeUpdate();
        }
    }
}
