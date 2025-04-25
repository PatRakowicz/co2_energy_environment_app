package com.example.app.dao;

import com.example.app.model.Utility;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class UtilityRecords implements DBQueries {
    private DBConn dbConn;
    private ArrayList<Utility> utilities = new ArrayList<>();
    private ResultSet resultSet;

    public UtilityRecords(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public boolean insertUtility(Utility utility) {
        String table = "utility";
        String columns = "buildingID, date, e_usage, e_cost, w_usage, w_cost, sw_cost, misc_cost";
        String e_usage, e_cost, w_usage, w_cost, sw_cost, misc_cost;

        if(utility.getElectricityUsage() < 0){
            e_usage = "NULL";
        }else{
            e_usage = String.format("%.2f", utility.getElectricityUsage());
        }

        if(utility.getElectricityCost() < 0){
            e_cost = "NULL";
        }else{
            e_cost = String.format("%.2f", utility.getElectricityCost());
        }

        if(utility.getWaterUsage() < 0){
            w_usage = "NULL";
        }else{
            w_usage = String.format("%.2f", utility.getWaterUsage());
        }

        if(utility.getWaterCost() < 0){
            w_cost = "NULL";
        }else{
            w_cost = String.format("%.2f", utility.getWaterCost());
        }

        if(utility.getSewageCost() < 0){
            sw_cost = "NULL";
        }else{
            sw_cost = String.format("%.2f", utility.getSewageCost());
        }

        if(utility.getMiscCost() < 0){
            misc_cost = "NULL";
        }else{
            misc_cost = String.format("%.2f", utility.getMiscCost());
        }

        String values = String.format(
                "%d, '%s', %s, %s, %s, %s, %s, %s",
                utility.getBuildingID(),
                new Date(utility.getDate().getTime()),
                e_usage,
                e_cost,
                w_usage,
                w_cost,
                sw_cost,
                misc_cost
        );
        return insert(table, columns, values, dbConn);
    }

    public ArrayList<Utility> getUtilities(int buildingID, LocalDate start, LocalDate end, DBConn dbConn) {
        String query = "SELECT * FROM utility "
                     + "WHERE buildingID = ? "
                     + "AND date >= ? AND date <= ?";
        try (PreparedStatement statement = this.dbConn.getConnection().prepareStatement(query)) {
            statement.setInt(1, buildingID);
            statement.setDate(2, Date.valueOf(start));
            statement.setDate(3, Date.valueOf(end));

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Utility utility = new Utility();

                utility.setUtilityID(resultSet.getInt("utilityID"));
                utility.setBuildingID(resultSet.getInt("buildingID"));
                utility.setDate(resultSet.getDate("date"));
                utility.setElectricityCost(resultSet.getFloat("e_cost"));
                utility.setWaterCost(resultSet.getFloat("w_cost"));
                utility.setSewageCost(resultSet.getFloat("sw_cost"));
                utility.setMiscCost(resultSet.getFloat("misc_cost"));
                utility.setElectricityUsage(resultSet.getFloat("e_usage"));
                utility.setWaterUsage(resultSet.getFloat("w_usage"));
                utility.setUsageGal(resultSet.getFloat("usage_gal"));
                utility.setUsageKwh(resultSet.getFloat("usage_kwh"));

                utilities.add(utility);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.printf("Caught SQL Error: %s", e);
        }

        return utilities;
    }
}
