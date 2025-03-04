package com.example.MainApp;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBController {
    @FXML
    private TextField ipField;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;

    private Connection conn;
    private boolean connectionSuccessful = false;
    private static final String ENV_FILE = "db_config.env";

    @FXML
    public void initialize() {
        if (loadDBConfig()) {
            attemptAutoConnect();
        }
    }

    public void handleSubmit(ActionEvent event) {
        String ip = ipField.getText().trim();
        String user = userField.getText().trim();
        String password = passwordField.getText().trim();

        if (ip.isEmpty() || user.isEmpty() || password.isEmpty()) {
            showAlert("Error", "IP, Username, and/or Password cannot be empty.");
            return;
        }

        conn = connectToDatabase(ip, user, password);

        if (conn != null) {
            connectionSuccessful = true;
            saveDBConfig(ip, user, password);
            showAlert("Success", "Database connection successful.");
        } else {
            connectionSuccessful = false;
            showAlert("Error", "Database connection failed.");
        }

        Stage stage = (Stage) ipField.getScene().getWindow();
        stage.close();
    }

    public Connection getConnection() {
        return conn;
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }

    private Connection connectToDatabase(String ip, String user, String password) {
        String url = "jdbc:mysql://" + ip + ":3306/WCU_Emissions";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            handleSQLException(e);
            return null;
        }
    }

    private void saveDBConfig(String ip, String user, String password) {
        try (FileWriter fw = new FileWriter(ENV_FILE)) {
            fw.write("ip=" + ip + "\n");
            fw.write("user=" + user + "\n");
            fw.write("password=" + password + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean loadDBConfig() {
        File file = new File(ENV_FILE);
        if (!file.exists()) return false;

        Properties prop = new Properties();
        try (FileReader fr = new FileReader(file)) {
            prop.load(fr);

            String ip = prop.getProperty("ip", "");
            String user = prop.getProperty("user", "");
            String password = prop.getProperty("password", "");

            if (!ip.isEmpty() && !user.isEmpty() && !password.isEmpty()) {
                conn = connectToDatabase(ip, user, password);
            }
            return connectionSuccessful;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void attemptAutoConnect() {
        if (conn != null) {
            connectionSuccessful = true;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}
