package com.example.app.dao;


import com.example.app.model.Building;
import com.example.app.model.Utility;

import java.sql.*;

public class MaterMeterLogic implements DBQueries {
    private final DBConn dbConn;
    private float usageRate, costRate;

    public MaterMeterLogic(DBConn c){dbConn = c;}

    // If the utility already exists returns the utilityID, if no utility exists returns 0,
    // if there is an error returns -1
    public int checkExistence(int year, int month, int id){
        Connection connection = dbConn.getConnection();
        String query = String.format(
                "SELECT * FROM utility WHERE YEAR(date) = %d AND MONTH(date) = %d AND buildingID = %d",
                year, month, id);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            if (result != null && result.next()) {
                return result.getInt("utility_ID");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    // returns true if creating new utility is successful
    // returns false if value fails to insert
    public boolean makeNew(Utility utility){
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

    // returns true if update was successful
    // returns false if update fails
    public boolean updateOld(Utility utility, Building building){
        float newCost = costRate * (float)building.getSqFT();
        float newUsage = usageRate * (float)building.getSqFT();

        String table = "utility";
        String setClause = String.format("e_cost = %.2f, e_usage = %.2f", newCost, newUsage);
        String condition = String.format("utilityID = %d", utility.getUtilityID());


        return false;
    }
}
