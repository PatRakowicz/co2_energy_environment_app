package com.example.app.dao;

import com.example.app.model.Gas;
import com.example.app.model.Log;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class LogRecords implements DBQueries {
    private DBConn dbConn;
    private ArrayList<String> logs = new ArrayList<>();
    private ResultSet resultSet;

    public LogRecords(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public boolean insertLog(Log log){
        String table = "logs";
        String columns = "timestamp, event";
        String values = String.format(
                "'%s', '%s'",
                new Date(log.getTimestamp().getTime()),
                log.getEvent()
        );
        return insert(table, columns, values, dbConn);
    }

    public ArrayList<String> getLogs() {
        logs.clear();

        String query = "SELECT * FROM logs ORDER BY logID DESC";
        try (PreparedStatement statement = this.dbConn.getConnection().prepareStatement(query)) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Log log = new Log();

                log.setLogID(resultSet.getInt("logID"));
                log.setTimestamp(resultSet.getDate("timestamp"));
                log.setEvent(resultSet.getString("event"));

                logs.add(log.getTimestamp() + ": " + log.getEvent());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.printf("Caught SQL Error: %s", e);
        }

        return logs;
    }

    public void clearLogs() {
        String query = "DELETE FROM logs";
        try (PreparedStatement statement = this.dbConn.getConnection().prepareStatement(query)) {
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.printf("Caught SQL Error: %s", e);
        }
    }
}

