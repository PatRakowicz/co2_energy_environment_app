package com.example.app.dao;

import com.example.app.model.Building;
import com.example.app.model.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateUtilityLogic implements DBQueries{
    private final DBConn dbConn;

    public UpdateUtilityLogic(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public Utility getUtilityForDate(int buildingID, int year, int month) {
        Utility utility = new Utility();
        Connection connection = dbConn.getConnection();
        if(connection == null){
            return null;
        }
        String table = "FROM utility ";
        String columns = "SELECT utilityID, buildingID, sw_cost, misc_cost, e_cost, w_cost, e_usage, w_usage, date ";
        String conditions = String.format(
                "WHERE YEAR(date) = %d AND MONTH(date) = %d AND buildingID = %d",
                year, month, buildingID);
        String query = columns + table + conditions;
        try (PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet resultSet = statement.executeQuery();

            if (resultSet != null && resultSet.next()) {
                utility.setUtilityID(resultSet.getInt("utilityID"));
                utility.setBuildingID(resultSet.getInt("buildingID"));
                utility.setSewageCost(resultSet.getFloat("sw_cost"));
                if(resultSet.wasNull()){
                    utility.setSewageCost(-1f);
                }
                utility.setMiscCost(resultSet.getFloat("misc_cost"));
                if(resultSet.wasNull()){
                    utility.setMiscCost(-1f);
                }
                utility.setElectricityCost(resultSet.getFloat("e_cost"));
                if(resultSet.wasNull()){
                    utility.setElectricityCost(-1f);
                }
                utility.setWaterCost(resultSet.getFloat("w_cost"));
                if(resultSet.wasNull()){
                    utility.setWaterCost(-1f);
                }
                utility.setElectricityUsage(resultSet.getFloat("e_usage"));
                if(resultSet.wasNull()){
                    utility.setElectricityUsage(-1f);
                }
                utility.setWaterUsage(resultSet.getFloat("w_usage"));
                if(resultSet.wasNull()){
                    utility.setWaterUsage(-1f);
                }
                utility.setDate(resultSet.getDate("date"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return utility;
    }

    public boolean updateUtility(Building building, Utility utility) {
        if (building == null || building.getBuildingID() == 0 || utility.getDate() == null) {
            System.out.println("Invalid input for updateUtility.");
            return false;
        }

        String table = "utility";
        String sw_cost, misc_cost, e_cost, w_cost, e_usage, w_usage;
        if(utility.getSewageCost() < 0){
            sw_cost = "NULL";
        }else{
            sw_cost = Float.toString(utility.getSewageCost());
        }

        if(utility.getMiscCost() < 0){
            misc_cost = "NULL";
        }else{
            misc_cost = Float.toString(utility.getMiscCost());
        }

        if(utility.getElectricityCost() < 0){
            e_cost = "NULL";
        }else{
            e_cost = Float.toString(utility.getElectricityCost());
        }

        if(utility.getWaterCost() < 0){
            w_cost = "NULL";
        }else{
            w_cost = Float.toString(utility.getWaterCost());
        }

        if(utility.getElectricityUsage() < 0){
            e_usage = "NULL";
        }else{
            e_usage = Float.toString(utility.getElectricityUsage());
        }

        if(utility.getWaterUsage() < 0){
            w_usage = "NULL";
        }else{
            w_usage = Float.toString(utility.getWaterUsage());
        }

        String setClause = String.format(
                "sw_cost = %s, misc_cost = %s, e_cost = %s, w_cost = %s, e_usage = %s, w_usage = %s",
                sw_cost, misc_cost, e_cost, w_cost, e_usage, w_usage);

        String condition =String.format("utilityID = %d", utility.getUtilityID());

        try{
            update(table, setClause, condition, dbConn);
        } catch (Exception e) {
            return false;
        }
        return true;
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
