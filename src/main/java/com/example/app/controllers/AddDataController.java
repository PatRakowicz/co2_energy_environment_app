package com.example.app.controllers;

import com.example.app.dao.*;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import com.example.app.model.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class AddDataController {
    private DBConn dbConn;
    private BuildingRecords buildingRecords;
    private ArrayList<Building> buildings;
    @FXML
    private Label electricityUsageError, electricityCostError, waterUsageError, waterCostError, sewageCostError,
            miscCostError, dateError, buildingError, currentChargesError, meterReadError, fromBillingError,
            toBillingError, billedCCFError, utilityDateLabel, buildingBoxLabel;
    @FXML
    private TextField electricityUsage, electricityCost, waterUsage, waterCost, sewageCost, miscCost, currentCharges,
            meterRead, billedCCF;
    @FXML
    private DatePicker utilityDate, fromBilling, toBilling;
    @FXML
    private ComboBox<Building> buildingComboBox;
    @FXML
    Button uploadUtilityCSVButton, downloadUtilityCSVButton, addUtilityButton, uplaodGasCSVButton, downloadGasCSVButton,
    addGasButton;
    @FXML
    private Tab utilityTab, gasTab, buildingTab;

    float eUsage, eCost, wUsage, wCost, sCost, mCost;
    LocalDate date;
    Building building;

    public AddDataController(){}

    public AddDataController(DBConn conn){
        this.dbConn = conn;
    }

    public void initialize() {
        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        FilteredBuildingBox buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        buildingComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            onChange();
        });
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

    public void clearGasErrors(){
        currentChargesError.setText(null);
        meterReadError.setText(null);
        fromBillingError.setText(null);
        toBillingError.setText(null);
        billedCCFError.setText(null);
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

    public void clearGasInputs(){
        currentCharges.setText(null);
        meterRead.setText(null);
        fromBilling.setValue(null);
        toBilling.setValue(null);
        billedCCF.setText(null);
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

    // This is where the error checking for the utility tab happens
    public boolean utilityValidity(){
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

    public boolean gasValidity(){
        boolean valid = true;

        if(buildingComboBox.getValue() == null){
            buildingError.setText("ERROR: building must be selected");
            valid = false;
        }
        else{
            building = buildingComboBox.getValue();
        }

        //check if textfields are proper

        //check is from billing and to billing dates make sense


        return valid;
    }

    public void addUtility(){
        clearUtilityErrors();
        if(utilityValidity()){
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
                clearUtilityInputs();
            } else {
                System.out.println("Failed to insert data.");
            }
            if(buildingComboBox.getValue().getName().equals("Master Meter")){
                averageMasterMeter(eCost, eUsage);
            }
        }

    }

    public void addGas(){

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
            UtilityCsvLogic uploader = new UtilityCsvLogic(dbConn);
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
            UtilityCsvLogic exporter = new UtilityCsvLogic(dbConn);
            exporter.exportCsvTemplate(file);
            System.out.println("CSV Template Exported.");
        }
    }

    public void handleUploadGasCsv(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File.");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null && dbConn != null) {
            GasCsvLogic uploader = new GasCsvLogic(dbConn);
            uploader.importGasCSV(file);
            System.out.println("CSV Upload Complete.");
        }
    }

    public void handleDownloadGasCsvTemplate(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV Template");
        fileChooser.setInitialFileName("gas_template.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null && dbConn != null) {
            GasCsvLogic exporter = new GasCsvLogic(dbConn);
            exporter.exportCsvTemplate(file);
            System.out.println("CSV Template Exported.");
        }
    }

    public void averageMasterMeter(float masterCost, float masterUsage){

    }

    public void tabChanged(){
        if(utilityTab != null && gasTab != null) {
            if (utilityTab.isSelected()) {
                setUtilityDateVisability(true);
                setBuildingBoxVisability(true);
            } else if (gasTab.isSelected()) {
                setUtilityDateVisability(false);
                setBuildingBoxVisability(true);
            }else if (buildingTab.isSelected()){
                setUtilityDateVisability(false);
                setBuildingBoxVisability(false);
            }
        }
    }

    public void setBuildingBoxVisability(boolean b){
        buildingBoxLabel.setVisible(b);
        buildingComboBox.setVisible(b);
        buildingError.setVisible(b);
    }

    public void setUtilityDateVisability(boolean b){
        utilityDate.setVisible(b);
        utilityDateLabel.setVisible(b);
        dateError.setVisible(b);
    }
}
