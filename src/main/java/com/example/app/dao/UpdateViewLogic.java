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
            System.out.println("Error fetching utility: " + e.getMessage());
        }
        return null;
    }

    public boolean updateUtility(Building building, Utility utility) {
        if (building == null || building.getBuildingID() == 0 || utility.getDate() == null) {
            System.out.println("Invalid input for updateUtility.");
            return false;
        }

        String query = "UPDATE utility SET e_usage = ?, e_cost = ?, w_usage = ?, w_cost = ?, sw_cost = ?, misc_cost = ? " +
                "WHERE buildingID = ? AND date = ?";
        try (PreparedStatement stmt = dbConn.getConnection().prepareStatement(query)) {
            stmt.setFloat(1, utility.getElectricityUsage());
            stmt.setFloat(2, utility.getElectricityCost());
            stmt.setFloat(3, utility.getWaterUsage());
            stmt.setFloat(4, utility.getWaterCost());
            stmt.setFloat(5, utility.getSewageCost());
            stmt.setFloat(6, utility.getMiscCost());
            stmt.setInt(7, building.getBuildingID());
            stmt.setDate(8, new java.sql.Date(utility.getDate().getTime()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating utility: " + e.getMessage());
            return false;
        }
    }

    public int getMinUtilityYear() {
        String query = "SELECT MIN(YEAR(date)) AS min_year FROM utility";
        try (PreparedStatement stmt = dbConn.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int year = rs.getInt("min_year");
                return rs.wasNull() ? LocalDate.now().getYear() : year;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching min year: " + e.getMessage());
        }
        return LocalDate.now().getYear();
    }
}
