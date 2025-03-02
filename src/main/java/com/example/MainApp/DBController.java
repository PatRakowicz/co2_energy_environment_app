package com.example.MainApp;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBController {
    @FXML
    private TextField ipField;
    @FXML
    private PasswordField passwordField;


    private boolean connectionSuccessful = false;

    public void handleSubmit(ActionEvent event) {
        // this is where the logic of submitting the information goes
        String ip = ipField.getText().trim();
        String password = passwordField.getText().trim();

        if (ip.isEmpty() || password.isEmpty()) {
            showAlert("Error", "IP and Password can not be empty.");
            return;
        }

        if (!ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            showAlert("Error", "Invalid IP format. Use standard IPv4 (e.g., 127.0.0.1).");
            return;
        }

        connectionSuccessful = testDataBaseConnection(ip, password);

        if (connectionSuccessful) {
            showAlert("Success", "Database connection successful.");
        } else {
            showAlert("Error", "Database connection failed.");
        }

        // Close window
        Stage stage = (Stage) ipField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean testDataBaseConnection(String ip, String password) {
        String url = "jdbc:mysql://" + ip + ":3306/WCU_Emissions"; // Update with correct database
        String user = "root"; // Replace with correct DB User

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return connection != null;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    private void handleSQLException(SQLException e) {
        String errorMessage = switch (e.getSQLState()) {
            case "28000" -> // Invalid authorization specification
                    "Invalid username or password.";
            case "08S01" -> // Communication link failure
                    "Cannot connect to database. Check IP and ensure MySQL is running.";
            case "42000" -> // Syntax error or access rule violation
                    "Access denied. Ensure your MySQL user has correct permissions.";
            default -> "Database connection failed: " + e.getMessage();
        };
        showAlert("Error", errorMessage);
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }
}
