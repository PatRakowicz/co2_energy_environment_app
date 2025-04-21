package com.example.app.dao;

import com.example.app.model.Building;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

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

                Building building = fetchBuildingByName(buildingName, conn);
                if (building == null) {
                    skippedCount++;
                    errorMessages.add("Line " + lineNumber + ": Unknown building \"" + buildingName + "\".");
                    continue;
                }

                try {
                    Float currentCharges = parseOrNull(parts[1]);
                    Date fromBilling = sdf.parse(parts[2]);
                    Date toBilling = sdf.parse(parts[3]);
                    Float meterRead = parseOrNull(parts[4]);
                    Float billedCCF = parseOrNull(parts[5]);

                    insertGas(conn, building.getBuildingID(), currentCharges, fromBilling, toBilling, meterRead, billedCCF);
                    insertedCount++;
                } catch (NumberFormatException | ParseException | SQLException e) {
                    skippedCount++;
                    errorMessages.add("Line " + lineNumber + ": Error parsing or inserting - " + e.getMessage());
                }
            }

        } catch (IOException | SQLException e) {
            errorMessages.add("File read error: " + e.getMessage());
        }

        showResultsAlert(insertedCount, skippedCount, errorMessages);
    }

    private Float parseOrNull(String value) {
        try {
            return value.trim().isEmpty() ? null : Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void insertGas(Connection conn, int buildingID, Float currentCharges, Date fromBilling,
                           Date toBilling, Float meterRead, Float billedCCF) throws SQLException {
        String query = "INSERT INTO gas (buildingID, current_charges, from_billing, to_billing, meter_read, billed_ccf) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, buildingID);
            stmt.setObject(2, currentCharges, Types.FLOAT);
            stmt.setDate(3, new java.sql.Date(fromBilling.getTime()));
            stmt.setDate(4, new java.sql.Date(toBilling.getTime()));
            stmt.setObject(5, meterRead, Types.FLOAT);
            stmt.setObject(6, billedCCF, Types.FLOAT);
            stmt.executeUpdate();
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
