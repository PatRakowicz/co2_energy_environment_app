package com.example.app.dao;


import com.example.app.model.Building;
import com.example.app.model.Log;
import com.example.app.model.Utility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class MasterMeterLogic implements DBQueries {
    private final DBConn dbConn;
    private float usageRate, costRate;
    private Utility masterUtility;
    private boolean printStats;
    int newEntries, updatedEntries, failedNewEntries, failedUpdatedEntries;


    public MasterMeterLogic(DBConn c, boolean p){dbConn = c; printStats = p;}

    public int getNewEntries(){
        return newEntries;
    }

    public int getUpdatedEntries(){
        return updatedEntries;
    }

    public void resetStats(){
        newEntries = 0;
        updatedEntries = 0;
        failedUpdatedEntries = 0;
        failedNewEntries = 0;
    }


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
        if(printStats){
            resetStats();
        }
        masterUtility = m;
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
                System.out.println("There was an Error with retrieving information");
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

        if(printStats) {
            showStats();
            LogRecords logRecords = new LogRecords(dbConn);
            Log log = new Log();
            log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
            logRecords.insertLog(log);
        }
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

    public void updateAllFrom(Date firstDate){
        resetStats();
        printStats = false;
        ArrayList<Utility> masterUtilities = getMasterUtilities(firstDate);
        for(Utility u : masterUtilities){
            singleUpdate(u);
        }

        showStats();

        printStats = true;
    }

    public ArrayList<Utility> getMasterUtilities(Date firstDate){
        ArrayList<Utility> masterUtilities = new ArrayList<Utility>();
        Connection connection = dbConn.getConnection();
        String query = String.format(
                "SELECT * FROM utility WHERE buildingID = 40 AND date > '%s' AND date <= current_date()",
                firstDate
        );

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Utility utility = new Utility();

                utility.setBuildingID(40);
                utility.setDate(resultSet.getDate("date"));
                utility.setElectricityCost(resultSet.getFloat("e_cost"));
                utility.setElectricityUsage(resultSet.getFloat("e_usage"));
                utility.setWaterCost(resultSet.getFloat("w_cost"));
                utility.setWaterUsage(resultSet.getFloat("w_usage"));
                utility.setSewageCost(resultSet.getFloat("sw_cost"));
                utility.setMiscCost(resultSet.getFloat("misc_cost"));

                masterUtilities.add(utility);
            }
        } catch (SQLException e) {
            System.out.printf("Caught SQL Error: %s", e);
            return null;
        }

        return masterUtilities;
    }

    public void showStats(){
        try {
            //load resource file into new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/stats.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UTILITY);

            Label one = (Label) scene.lookup("#one");
            Label two = (Label) scene.lookup("#two");
            Label three = (Label) scene.lookup("#three");
            Label four = (Label) scene.lookup("#four");
            one.setText(String.format("Successfully Inserted: %d", newEntries));
            two.setText(String.format("Failed To Insert: %d", failedNewEntries));
            three.setText(String.format("Successfully Updated: %d", updatedEntries));
            four.setText(String.format("Failed To Update: %d", failedUpdatedEntries));

            //if user clicks with mouse, close the stage
            root.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    stage.close();
                }
            });

            //if focus is lost close the stage
            stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    stage.close();
                }
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMasterMeterDate(Date date){
        String table = "building";
        String setClause = String.format("date = '%s'", date);
        String condition = "buildingID = 40";
        update(table, setClause, condition, dbConn);
    }
}
