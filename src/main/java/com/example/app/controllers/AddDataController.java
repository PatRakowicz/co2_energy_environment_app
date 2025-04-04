package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.UtilityRecords;
import com.example.app.dao.CsvLogic;
import com.example.app.model.Building;
import com.example.app.model.FilteredBuildingBox;
import com.example.app.model.Gas;
import com.example.app.model.Utility;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class AddDataController extends ApplicationController {
    private BuildingRecords buildingRecords;
    @FXML
    private Label electricityUsageError;
    @FXML
    private Label electricityCostError;
    @FXML
    private Label waterUsageError;
    @FXML
    private Label waterCostError;
    @FXML
    private Label sewageCostError;
    @FXML
    private Label miscCostError;
    @FXML
    private Label dateError;
    @FXML
    private Label buildingError;
    @FXML
    private TextField electricityUsage;
    @FXML
    private TextField electricityCost;
    @FXML
    private TextField waterUsage;
    @FXML
    private TextField waterCost;
    @FXML
    private TextField sewageCost;
    @FXML
    private TextField miscCost;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Building> buildingComboBox;
    private FilteredBuildingBox buildingBox;

    @FXML Button uploadCsvButton;

    float eUsage;
    float eCost;
    float wUsage;
    float wCost;
    float sCost;
    float mCost;
    LocalDate date;
    Building building;



    public void clearErrors(){
        electricityUsageError.setText("");
        electricityCostError.setText("");
        waterUsageError.setText("");
        waterCostError.setText("");
        sewageCostError.setText("");
        miscCostError.setText("");
        dateError.setText("");
        buildingError.setText("");
    }


    public void clearInputs(){
        electricityUsage.setText("");
        electricityCost.setText("");
        waterUsage.setText("");
        waterCost.setText("");
        sewageCost.setText("");
        miscCost.setText("");
        datePicker.setValue(null);
    }

    // This is where the error checking happens
    public boolean validity(){
        boolean valid = true;
        try {
            eUsage = Float.parseFloat(electricityUsage.getText());
        } catch (NumberFormatException ex) {
            if(!electricityUsage.getText().isEmpty()){
                electricityUsageError.setText("ERROR: Electricity Usage must be a number");
                valid = false;
            }
        }

        try {
            eCost = Float.parseFloat(electricityCost.getText());
        } catch (NumberFormatException e) {
            if(!electricityCost.getText().isEmpty()){
                electricityCostError.setText("ERROR: Electricity Cost must be a number");
                valid = false;
            }
        }

        try {
            wUsage = Float.parseFloat(waterUsage.getText());
        } catch (NumberFormatException e) {
            if(!waterUsage.getText().isEmpty()){
                waterUsageError.setText("ERROR: Water Usage must be a number");
                valid = false;
            }
        }

        try {
            wCost = Float.parseFloat(waterCost.getText());
        } catch (NumberFormatException e) {
            if(!waterCost.getText().isEmpty()){
                waterCostError.setText("ERROR: Water Cost must be a number");
                valid = false;
            }
        }

        try {
            sCost = Float.parseFloat(sewageCost.getText());
        } catch (NumberFormatException e) {
            if(!sewageCost.getText().isEmpty()){
                sewageCostError.setText("ERROR: Sewage Cost must be a number");
                valid = false;
            }
        }

        try {
            mCost = Float.parseFloat(miscCost.getText());
        } catch (NumberFormatException e) {
            if(!miscCost.getText().isEmpty()){
                miscCostError.setText("ERROR: Misc. Cost must be a number");
                valid = false;
            }
        }

        if(datePicker.getValue() == null){
            dateError.setText("ERROR: invalid date");
            valid = false;
        }
        else{
            date = datePicker.getValue();
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

    public void add(){
        clearErrors();
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
                System.out.println("Data inserted.");
                clearInputs();
            } else {
                System.out.println("Failed to insert data.");
            }
        }

    }

    @Override
    public void initialize() {
        super.initialize();

        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(super.dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);
    }

    public void onChange(){
        if(buildingComboBox.getValue() != null){
            buildingBox.lastNotNull = buildingComboBox.getValue();
        }
        if(buildingComboBox.getValue() != null && datePicker.getValue() != null) {
            String name = buildingComboBox.getValue().getName();
            Date checkDate = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            for (int i = 0; i < buildings.size(); i++) {
                String checkName = buildings.get(i).getName();
                if (Objects.equals(checkName, name)) {
                    Date checkDateStart = buildings.get(i).getStartShared();
                    Date checkDateEnd = buildings.get(i).getEndShared();
                    if(checkDateStart != null) {
                        if (checkDateStart.before(checkDate)) {
                            if (checkDateEnd == null) {
                                electricityCost.setDisable(true);
                                electricityUsage.setDisable(true);
                                electricityCost.setText("");
                                electricityUsage.setText("");
                            } else if (checkDateEnd.after(checkDate)) {
                                electricityCost.setDisable(true);
                                electricityUsage.setDisable(true);
                                electricityCost.setText("");
                                electricityUsage.setText("");
                            } else {
                                electricityUsage.setDisable(false);
                                electricityCost.setDisable(false);
                            }

                        } else {
                            electricityUsage.setDisable(false);
                            electricityCost.setDisable(false);
                        }
                    }
                    else{
                        electricityUsage.setDisable(false);
                        electricityCost.setDisable(false);
                    }
                    break;
                }
            }
        }
    }

    @FXML
    public void handleUploadCsv() {
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
    public void handleDownloadCsvTemplate() {
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
}
