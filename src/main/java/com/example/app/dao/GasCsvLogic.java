package com.example.app.dao;

import com.example.app.model.Building;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;


public class GasCsvLogic implements DBQueries {
    private final DBConn dbConn;

    public GasCsvLogic(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public void importGasCSV(File file) {
        int insertedCount = 0;
        int skippedCount = 0;
        ArrayList<String> errorMessages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                errorMessages.add("No active DB connection.");
                showResultsAlert(insertedCount, skippedCount, errorMessages);
                return;
            }

            String headerLine = reader.readLine();
            if (headerLine == null) {
                errorMessages.add("Empty CSV file.");
                showResultsAlert(insertedCount, skippedCount, errorMessages);
                return;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                if (parts.length > 3 && (parts[2].equalsIgnoreCase("From Billing (DD/MM/YYYY)") || parts[3].equalsIgnoreCase("To Billing (DD/MM/YYYY)"))) {
                    skippedCount++;
                    errorMessages.add("Skipped header row detected in upload.");
                    continue;
                }

                if (parts.length < 6) {
                    skippedCount++;
                    errorMessages.add("Line " + lineNumber + ": Incomplete data.");
                    continue;
                }

                String buildingName = parts[0];
                if (buildingName.isEmpty()) {
                    skippedCount++;
                    errorMessages.add("Line " + lineNumber + ": Missing building name.");
                    continue;
                }

                boolean hasData = false;
                for (int i = 1; i <= 5; i++) {
                    if (!parts[i].isEmpty()) {
                        hasData = true;
                        break;
                    }
                }
                if (!hasData) {
                    skippedCount++;
                    System.out.printf("Skipping row with no data: %s%n", line);
                    continue;
                }

                Building building = fetchBuildingByName(buildingName, conn);
                if (building == null) {
                    skippedCount++;
                    errorMessages.add("Line " + lineNumber + ": Unknown building \"" + buildingName + "\".");
                    continue;
                }

                try {
                    Float currentCharges = parseOrNull(parts[1]);
                    Date fromBilling = parseDateOrNull(parts[2]);
                    Date toBilling = parseDateOrNull(parts[3]);
                    Float meterRead = parseOrNull(parts[4]);
                    Float billedCCF = parseOrNull(parts[5]);

                    insertGas(conn, building.getBuildingID(), currentCharges, fromBilling, toBilling, meterRead, billedCCF);
                    insertedCount++;
                } catch (NumberFormatException | SQLException e) {
                    skippedCount++;
                    errorMessages.add("Line " + lineNumber + ": Error parsing or inserting - " + e.getMessage());
                }
            }

        } catch (IOException | SQLException e) {
            errorMessages.add("File read error: " + e.getMessage());
        }

        showResultsAlert(insertedCount, skippedCount, errorMessages);
    }

    public void exportCsvTemplate(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.out.println("No active DB connection");
                return;
            }

            writer.write(String.join(",", new String[]{
                    "Building Name", "Current Charges", "From Billing (DD/MM/YYYY)", "To Billing (DD/MM/YYYY)", "Meter Read", "Billed CCF"
            }));
            writer.newLine();

            String query = "SELECT name FROM building";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String buildingName = rs.getString("name");
                    writer.write(buildingName + ",,,,,");
                    writer.newLine();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertGas(Connection conn, int buildingID, Float currentCharges, Date fromBilling,
                           Date toBilling, Float meterRead, Float billedCCF) throws SQLException {
        String query = "INSERT INTO gas (buildingID, current_charges, from_billing, to_billing, meter_read, billed_ccf) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, buildingID);
            stmt.setObject(2, currentCharges, Types.FLOAT);
            stmt.setObject(3, fromBilling, Types.DATE);
            stmt.setObject(4, toBilling, Types.DATE);
            stmt.setObject(5, meterRead, Types.FLOAT);
            stmt.setObject(6, billedCCF, Types.FLOAT);
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
            alert.setTitle("Gas CSV Import Complete");
            alert.setHeaderText(null);

            StringBuilder message = new StringBuilder();
            message.append(insertedCount).append(" entries were successfully added.\n");
            message.append(skippedCount).append(" rows were skipped.\n");

            if (!errors.isEmpty()) {
                message.append("\nDetails:\n");
                for (String error : errors) {
                    message.append("- ").append(error).append("\n");
                }
            }

            alert.setContentText(message.toString());
            alert.showAndWait();
        });
    }
}
