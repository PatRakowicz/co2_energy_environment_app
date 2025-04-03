package com.example.app.dao;

import com.example.app.model.Building;
import com.example.app.model.Utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateViewLogic implements DBQueries {
    private final DBConn dbConn;

    public UpdateViewLogic(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public boolean insertUtility(Building building, Utility utility) {
        if (building == null || building.getBuildingID() == 0 || utility.getDate() == null) {
            System.out.println("Invalid input: building and date must be provided.");
            return false;
        }

        String query = "INSERT INTO utility (buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, misc_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConn.getConnection().prepareStatement(query)) {
            stmt.setInt(1, building.getBuildingID());
            stmt.setDate(2, new java.sql.Date(utility.getDate().getTime()));
            stmt.setFloat(3, utility.getElectricityUsage());
            stmt.setFloat(4, utility.getElectricityCost());
            stmt.setFloat(5, utility.getWaterUsage());
            stmt.setFloat(6, utility.getWaterCost());
            stmt.setFloat(7, utility.getSewageCost());
            stmt.setFloat(8, utility.getMiscCost());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utility getUtilityForDate(int buildingID, LocalDate date) {
        String query = "SELECT * FROM utility WHERE buildingID = ? AND date = ?";
        try (PreparedStatement stmt = dbConn.getConnection().prepareStatement(query)) {
            stmt.setInt(1, buildingID);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utility utility = new Utility();
                utility.setElectricityUsage(rs.getFloat("e_usage"));
                utility.setElectricityCost(rs.getFloat("e_cost"));
                utility.setWaterUsage(rs.getFloat("w_usage"));
                utility.setWaterCost(rs.getFloat("w_cost"));
                utility.setSewageCost(rs.getFloat("sw_cost"));
                utility.setMiscCost(rs.getFloat("misc_cost"));
                return utility;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Additional custom queries for update view can go here later.
}
