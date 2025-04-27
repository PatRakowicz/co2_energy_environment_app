package com.example.app.dao;


import com.example.app.model.Building;
import com.example.app.model.Utility;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class MasterMeterLogic implements DBQueries {
    private final DBConn dbConn;
    private float usageRate, costRate;
    private Utility masterUtility;

    public MasterMeterLogic(DBConn c){dbConn = c;}


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
                return result.getInt("utilityID");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // returns true if creating new utility is successful
    // returns false if value fails to insert
    public boolean makeNew(Utility utility){
        String table = "utility";
        String columns = "buildingID, date, e_usage, e_cost";

        String values = String.format(
                "%d, '%s', %s, %s",
                utility.getBuildingID(),
                new Date(utility.getDate().getTime()),
                utility.getElectricityUsage(),
                utility.getElectricityCost()
        );
        return insert(table, columns, values, dbConn);
    }

    // returns true if update was successful
    // returns false if update fails
    public boolean updateOld(int utilityID, Building building){
        float newCost = costRate * (float)building.getSqFT();
        float newUsage = usageRate * (float)building.getSqFT();

        String table = "utility";
        String setClause = String.format("e_cost = %.2f, e_usage = %.2f", newCost, newUsage);
        String condition = String.format("utilityID = %d", utilityID);

        return update(table, setClause, condition, dbConn);
    }

    public void singleUpdate(Utility m){
        masterUtility = m;
        int newEntries = 0;
        int updatedEntries = 0;
        int failedNewEntries = 0;
        int failedUpdatedEntries = 0;
        LocalDate masterDate = new Date(masterUtility.getDate().getTime()).toLocalDate();
        int year = masterDate.getYear();
        int month = masterDate.getMonthValue();

        ArrayList<Building> buildings = getAllBuildingsForDate();
        getUsageAndCostRates(buildings);

        for(Building b : buildings){
            int id = checkExistence(year, month, b.getBuildingID());
            // make new utility
            if(id == 0){
                Utility newUtility = new Utility();
                newUtility.setBuildingID(b.getBuildingID());
                newUtility.setDate(masterUtility.getDate());
                newUtility.setElectricityUsage(b.getSqFT() * usageRate);
                newUtility.setElectricityCost(b.getSqFT() * costRate);
                if(makeNew(newUtility)){
                    newEntries++;
                }else{
                    failedNewEntries++;
                }
            }
            // there was an error
            else if (id == -1){
                //TODO print error messages?
            }
            // update existing utility
            else{
                if(updateOld(id, b)){
                    updatedEntries++;
                }else{
                    failedUpdatedEntries++;
                }
            }
        }

        System.out.println(String.format("Successful Inserts = %d", newEntries));
        System.out.println(String.format("Filed Inserts = %d", failedNewEntries));
        System.out.println(String.format("Successful Updates = %d", updatedEntries));
        System.out.println(String.format("Failed Updates = %d", failedUpdatedEntries));


        //TODO show success stats
    }

    public ArrayList<Building> getAllBuildingsForDate(){
        ArrayList<Building> buildings = new ArrayList<Building>();
        Connection connection = dbConn.getConnection();
        Date mDate = new Date(masterUtility.getDate().getTime());
        String query = String.format(
                "SELECT * FROM building WHERE start_shared <= '%s' AND (end_shared IS NULL OR end_shared > '%s')",
                mDate, mDate
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Building building = new Building();

                building.setDate(resultSet.getDate("date"));
                building.setLocation(resultSet.getString("location"));
                building.setSqFT(resultSet.getInt("sqFT"));
                building.setBuildingID(resultSet.getInt("buildingID"));
                building.setName(resultSet.getString("name"));
                building.setStartShared(resultSet.getDate("start_shared"));
                building.setEndShared(resultSet.getDate("end_shared"));

                buildings.add(building);
            }
        } catch (SQLException e) {
            System.out.printf("Caught SQL Error: %s", e);
            return null;
        }

        return buildings;
    }

    public void getUsageAndCostRates(ArrayList<Building> buildings){
        float total = 0;
        for(Building b : buildings){
            total += b.getSqFT();
        }
        usageRate = masterUtility.getElectricityUsage() / total;
        costRate = masterUtility.getElectricityCost() / total;
    }
}
