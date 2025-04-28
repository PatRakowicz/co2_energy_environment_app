package com.example.app.dao;

import com.example.app.model.Building;
import com.example.app.model.Utility;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class UtilityCsvLogic implements DBQueries {
    private final String[] HEADERS = {
            "Building Name", "Water Usage", "Water Cost", "Electricity Usage", "Electricity Cost",
            "Sewage Cost", "Misc Cost"
    };
    private DBConn dbConn;
    private boolean masterMeterInserted;

    public UtilityCsvLogic(DBConn dbConn) {
        this.dbConn = dbConn;
    }


    public void importUtilityCSV(File file) {
        Utility masterUtility = new Utility();
        int insertedCount = 0;
        int skippedCount = 0;
        ArrayList<String> errorMessages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("No active DB connection");
                return;
            }

            String headerLine = reader.readLine();
            if (headerLine == null) {
                errorMessages.add("Empty CSV file.");
                showResultsAlert(insertedCount, skippedCount, errorMessages);
                return;
            }

            String[] headers = headerLine.split(",", -1);
            String sharedDateString = headers[headers.length - 1].trim();
            Date sharedDate = parseDateOrNull(sharedDateString);
            if (sharedDate == null) {
                errorMessages.add("Invalid or missing shared date in header: " + sharedDateString);
                showResultsAlert(insertedCount, skippedCount, errorMessages);
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                String buildingName = parts[0];
                if (buildingName.isEmpty()) {
                    skippedCount++;
                    continue;
                }

                boolean hasData = false;
                for (int i = 1; i <= 7 && i < parts.length; i++) {
                    if (!parts[i].isEmpty()) {
                        hasData = true;
                        break;
                    }
                }
                if (!hasData) {
                    System.out.printf("Skipping row with no data: %s%n", line);
                    skippedCount++;
                    continue;
                }

                Building building = fetchBuildingByName(buildingName, conn);
                if (building == null) {
                    System.out.printf("Skipping unknown building: %s%n", buildingName);
                    skippedCount++;
                    continue;
                }

                Utility utility = new Utility();
                utility.setDate(sharedDate);
                utility.setBuildingID(building.getBuildingID());
                utility.setWaterUsage(parts.length > 1 ? parseOrNull(parts[1]) : null);
                utility.setWaterCost(parts.length > 2 ? parseOrNull(parts[2]) : null);
                utility.setElectricityUsage(parts.length > 3 ? parseOrNull(parts[3]) : null);
                utility.setElectricityCost(parts.length > 4 ? parseOrNull(parts[4]) : null);
                utility.setSewageCost(parts.length > 5 ? parseOrNull(parts[5]) : null);
                utility.setUsageGal(parts.length > 6 ? parseOrNull(parts[6]) : null);
                utility.setMiscCost(parts.length > 7 ? parseOrNull(parts[7]) : null);

                if (building.getName().equalsIgnoreCase("Master Meter")) {
                    boolean hasRequiredFields =
                            (utility.getElectricityUsage() != null && utility.getElectricityUsage() != 0) ||
                                    (utility.getElectricityCost() != null && utility.getElectricityCost() != 0) ||
                                    (utility.getMiscCost() != null && utility.getMiscCost() != 0);

                    if (!hasRequiredFields) {
                        skippedCount++;
                        errorMessages.add("Master Meter entry must have at least Electricity Usage, Electricity Cost, or Misc Cost filled.");
                        continue;
                    }

                    masterMeterInserted = true;
                    masterUtility = utility;
                }

                insertUtility(conn, building, utility);
                insertedCount++;
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            errorMessages.add("General error during file processing: " + e.getMessage());
            return;
        }

        // Show results in alert
        showResultsAlert(insertedCount, skippedCount, errorMessages);

        if (masterMeterInserted) {
            MasterMeterLogic masterMeterLogic = new MasterMeterLogic(dbConn, false);
            masterMeterLogic.singleUpdate(masterUtility);
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

    private void insertUtility(Connection conn, Building building, Utility utility) throws SQLException {
        String insert = "INSERT INTO utility (buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, misc_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, building.getBuildingID());
            stmt.setDate(2, new java.sql.Date(utility.getDate().getTime()));
            stmt.setObject(3, utility.getElectricityUsage(), Types.FLOAT);
            stmt.setObject(4, utility.getElectricityCost(), Types.FLOAT);
            stmt.setObject(5, utility.getWaterUsage(), Types.FLOAT);
            stmt.setObject(6, utility.getWaterCost(), Types.FLOAT);
            stmt.setObject(7, utility.getSewageCost(), Types.FLOAT);
            stmt.setObject(8, utility.getMiscCost(), Types.FLOAT);

            stmt.executeUpdate();
        }
    }

    private Float parseOrNull(String value) throws NumberFormatException {
        if (value.trim().isEmpty()) return null;
        String sanitized = value.replaceAll("[$,%]", "").trim();
        if (!sanitized.matches("-?\\d*(\\.\\d+)?")) {
            throw new NumberFormatException("Invalid numeric value: " + value);
        }
        return Float.parseFloat(sanitized);
    }

    private Date parseDateOrNull(String value) {
        try {
            if (value.trim().isEmpty()) return null;

            value = value.trim();

            if (!value.matches("\\d{2}/\\d{2}/\\d{4}")) return null;

            String[] parts = value.split("/");
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

    private void showResultsAlert(int insertedCount, int skippedCount, ArrayList<String> errors) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CSV Import Complete");
            alert.setHeaderText(null);

            StringBuilder message = new StringBuilder();
            message.append(insertedCount).append(" entries were successfully added.\n");
            message.append(skippedCount).append(" rows were skipped.\n");

            if (!errors.isEmpty()) {
                message.append("\nDetails:\n");
                for (String error : errors) message.append("- ").append(error).append("\n");
            }

            alert.setContentText(message.toString());
            alert.showAndWait();
        });
    }
}
