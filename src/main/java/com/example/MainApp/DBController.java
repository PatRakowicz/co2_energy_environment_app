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
    private TextField userField;
    @FXML
    private PasswordField passwordField;

    private boolean connectionSuccessful = false;

    public void handleSubmit(ActionEvent event) {
        String ip = ipField.getText().trim();
        String user = userField.getText().trim();
        String password = passwordField.getText().trim();

        if (ip.isEmpty() || user.isEmpty() || password.isEmpty()) {
            showAlert("Error", "IP, Username, and/or Password cannot be empty.");
            return;
        }


        connectionSuccessful = testDatabaseConnection(ip, user, password);

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

    private boolean testDatabaseConnection(String ip, String user, String password) {
        String url = "jdbc:mysql://" + ip + ":3306/WCU_Emissions";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return connection != null;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    private void handleSQLException(SQLException e) {
        String errorMessage = switch (e.getSQLState()) {
            case "28000" -> "Invalid user or password.";
            case "08S01" -> "Cannot connect to database. Check IP and ensure MySQL is running.";
            case "42000" -> "Access denied. Ensure your MySQL user has correct permissions.";
            default -> "Database connection failed: " + e.getMessage();
        };
        showAlert("Error", errorMessage);
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }
}
