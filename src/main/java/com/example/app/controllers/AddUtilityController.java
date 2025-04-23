package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.CsvLogic;
import com.example.app.dao.DBConn;
import com.example.app.dao.UtilityRecords;
import com.example.app.model.Building;
import com.example.app.model.Utility;
import com.example.app.utils.Alerts;
import com.example.app.utils.FilteredBuildingBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class AddUtilityController implements Alerts {
    private DBConn dbConn;
    private ArrayList<Building> buildings;
    @FXML
    private Label electricityUsageError, electricityCostError, waterUsageError, waterCostError, sewageCostError,
            miscCostError, dateError, buildingError;
    @FXML
    private TextField electricityUsage, electricityCost, waterUsage, waterCost, sewageCost, miscCost;
    @FXML
    private DatePicker utilityDate;
    @FXML
    private ComboBox<Building> buildingComboBox;
    @FXML
    Button uploadUtilityCSVButton, downloadUtilityCSVButton, addUtilityButton;

    float eUsage, eCost, wUsage, wCost, sCost, mCost;
    LocalDate date;
    Building building;
    FilteredBuildingBox buildingBox;
    private BuildingRecords buildingRecords;

    public AddUtilityController(){}

    public AddUtilityController(DBConn c){
        dbConn = c;
    }

    public void initialize() {
        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        buildingComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            onChange();
        });
    }

    public void setBuildings(ArrayList<Building> buildings) {
        buildingBox.setList(buildings);
    }

    private void setDisabledAll(boolean d){
        electricityUsage.setDisable(d);
        electricityCost.setDisable(d);
        waterUsage.setDisable(d);
        waterCost.setDisable(d);
        sewageCost.setDisable(d);
        miscCost.setDisable(d);
    }

    private void setDisabledOnMaster(){
        // disable electricity fields
        electricityUsage.setDisable(true);
        electricityCost.setDisable(true);
        // clear electricity fields so no electricity data gets added by mistake
        electricityUsage.setText(null);
        electricityCost.setText(null);
        // enable remaining fields in case they were disabled
        waterCost.setDisable(false);
        waterUsage.setDisable(false);
        sewageCost.setDisable(false);
        miscCost.setDisable(false);
    }

    public void clearUtilityInputs(){
        electricityUsage.setText(null);
        electricityCost.setText(null);
        waterUsage.setText(null);
        waterCost.setText(null);
        sewageCost.setText(null);
        miscCost.setText(null);
        utilityDate.setValue(null);
    }

    public void clearUtilityErrors(){
        electricityUsageError.setText(null);
        electricityCostError.setText(null);
        waterUsageError.setText(null);
        waterCostError.setText(null);
        sewageCostError.setText(null);
        miscCostError.setText(null);
        dateError.setText(null);
        buildingError.setText(null);
    }

    // This is where the error checking happens
    public boolean validity(){
        boolean valid = true;
        if(electricityUsage.getText().isEmpty()){
            eUsage = -1;
        }else {
            try {
                eUsage = Float.parseFloat(electricityUsage.getText());
                if(eUsage < 0){
                    electricityUsageError.setText("ERROR: Electricity Usage can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException ex) {
                if (!electricityUsage.getText().isEmpty()) {
                    electricityUsageError.setText("ERROR: Electricity Usage must be a number");
                    valid = false;
                }
            }
        }

        if(electricityCost.getText().isEmpty()){
            eCost = -1;
        }else {
            try {
                eCost = Float.parseFloat(electricityCost.getText());
                if(eCost < 0){
                    electricityCostError.setText("ERROR: Electricity Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (!electricityCost.getText().isEmpty()) {
                    electricityCostError.setText("ERROR: Electricity Cost must be a number");
                    valid = false;
                }
            }
        }

        if(waterUsage.getText().isEmpty()){
            wUsage = -1;
        }else {
            try {
                wUsage = Float.parseFloat(waterUsage.getText());
                if(wUsage < 0){
                    waterUsageError.setText("ERROR: Water Usage can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (!waterUsage.getText().isEmpty()) {
                    waterUsageError.setText("ERROR: Water Usage must be a number");
                    valid = false;
                }
            }
        }

        if(waterCost.getText().isEmpty()){
            wCost = -1;
        }else {
            try {
                wCost = Float.parseFloat(waterCost.getText());
                if(wCost < 0){
                    waterCostError.setText("ERROR: Water Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (!waterCost.getText().isEmpty()) {
                    waterCostError.setText("ERROR: Water Cost must be a number");
                    valid = false;
                }
            }
        }

        if(sewageCost.getText().isEmpty()){
            sCost = -1;
        }else {
            try {
                sCost = Float.parseFloat(sewageCost.getText());
                if(sCost < 0){
                    sewageCostError.setText("ERROR: Sewage Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (!sewageCost.getText().isEmpty()) {
                    sewageCostError.setText("ERROR: Sewage Cost must be a number");
                    valid = false;
                }
            }
        }

        if(miscCost.getText().isEmpty()){
            mCost = -1;
        }else {
            try {
                mCost = Float.parseFloat(miscCost.getText());
                if(mCost < 0){
                    miscCostError.setText("ERROR: Misc. Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (!miscCost.getText().isEmpty()) {
                    miscCostError.setText("ERROR: Misc. Cost must be a number");
                    valid = false;
                }
            }
        }

        if(utilityDate.getValue() == null){
            dateError.setText("ERROR: invalid date");
            valid = false;
        }
        else{
            date = utilityDate.getValue();
        }

        if(buildingComboBox.getValue() == null){
            buildingError.setText("ERROR: building must be selected");
            valid = false;
        }
        else{
            building = buildingComboBox.getValue();
        }


        return valid;
    }

    public void addUtility(){
        clearUtilityErrors();
        if(validity()){
            Utility utility = new Utility();
            utility.setBuildingID(building.getBuildingID());
            utility.setDate(java.sql.Date.valueOf(date));
            utility.setElectricityUsage(eUsage);
            utility.setElectricityCost(eCost);
            utility.setWaterUsage(wUsage);
            utility.setWaterCost(wCost);
            utility.setSewageCost(sCost);
            utility.setMiscCost(mCost);

            UtilityRecords utilityRecords = new UtilityRecords(dbConn);
            boolean success = utilityRecords.insertUtility(utility);

            if (success) {
                // Log inserted data here
                insertSuccessful();
                clearUtilityInputs();
                if(buildingComboBox.getValue().getName().equals("Master Meter")){
                    averageMasterMeter(eCost, eUsage);
                }
            } else {
                insertFail();
            }
        }

    }

    public void onChange(){
        if(buildingComboBox.getValue() != null && utilityDate.getValue() != null) {
            if(buildingComboBox.getValue().getName().equals("Master Meter")){
                electricityUsage.setDisable(false);
                electricityCost.setDisable(false);
                miscCost.setDisable(false);
                waterUsage.setDisable(true);
                waterUsage.setText(null);
                waterCost.setDisable(true);
                waterCost.setText(null);
                sewageCost.setDisable(true);
                sewageCost.setText(null);
            }
            else {
                Date checkDate = Date.from(utilityDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date checkDateStart = buildingComboBox.getValue().getStartShared();
                Date checkDateEnd = buildingComboBox.getValue().getEndShared();

                if(checkDateStart == null && checkDateEnd == null){
                    setDisabledAll(false);
                }
                else if(checkDateStart != null && checkDateEnd == null){
                    setDisabledOnMaster();
                }
                else if(checkDateStart != null && checkDateEnd != null){
                    if(checkDateEnd.before(checkDate) || checkDateEnd == checkDate){
                        setDisabledAll(false);
                    }
                    else{
                        setDisabledOnMaster();
                    }
                }
            }
        }
    }

    @FXML
    public void handleUploadUtilityCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File.");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null && dbConn != null) {
            CsvLogic uploader = new CsvLogic(dbConn);
            uploader.importUtilityCSV(file);
            System.out.println("CSV Upload Complete.");
        }
    }

    @FXML
    public void handleDownloadUtilityCsvTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV Template");
        fileChooser.setInitialFileName("utility_template.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null && dbConn != null) {
            CsvLogic exporter = new CsvLogic(dbConn);
            exporter.exportCsvTemplate(file);
            System.out.println("CSV Template Exported.");
        }
    }

    public void averageMasterMeter(float masterCost, float masterUsage){

    }
}
