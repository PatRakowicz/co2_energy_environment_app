package com.example.app.dao;

import com.example.app.controllers.DBController;
import com.example.app.model.Utility;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CsvUploader implements DBQueries{
    private DBController dbController;

    public CsvUploader(DBController dbController) {
        this.dbController = dbController;
    }

    public void uploadUtilityCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int fetchBuildingID(String name, Connection conn) throws Exception {

    return 1;
    }

    private void insertUtilityRecord(Connection conn, Utility utility) throws Exception {
        String insert = "INSERT INTO utility (buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, usage_gal, misc_cost)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.executeUpdate();
        }
    }
}
