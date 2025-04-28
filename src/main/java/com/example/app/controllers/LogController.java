package com.example.app.controllers;
import com.example.app.dao.DBConn;
import com.example.app.dao.GasCsvLogic;
import com.example.app.dao.LogRecords;
import com.example.app.model.Log;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;

public class LogController {
    private DBConn dbConn;
    private LogRecords logRecords;

    @FXML private ListView<String> logList;

    public LogController() {}

    public LogController(DBConn dbConn) {
        this.dbConn = dbConn;
        logRecords = new LogRecords(this.dbConn);
    }

    @FXML public void initialize() {
        ArrayList<String> logs = logRecords.getLogs();
        logList.getItems().setAll(logs);
    }

    @FXML public void downloadLogs(ActionEvent event) {
        // This needs to change to a .txt with all logs | PLACEHOLDER FOR NOW!
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV Template");
        fileChooser.setInitialFileName("gas_template.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null && dbConn != null) {
            GasCsvLogic exporter = new GasCsvLogic(dbConn);
            exporter.exportCsvTemplate(file);

            LogRecords logRecords = new LogRecords(dbConn);
            Log log = new Log();
            log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
            log.setEvent("All logs downloaded to file.");
            logRecords.insertLog(log);
        }
    }

    @FXML public void clearLogs(ActionEvent event) {
        logRecords.clearLogs();

        LogRecords logRecords = new LogRecords(dbConn);
        Log log = new Log();
        log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
        log.setEvent("All logs were cleared.");
        logRecords.insertLog(log);

        ArrayList<String> logs = logRecords.getLogs();
        logList.getItems().setAll(logs);
    }

}
