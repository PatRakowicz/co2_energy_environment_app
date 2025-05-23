package com.example.app.controllers;

import com.example.app.dao.*;
import com.example.app.model.Building;
import com.example.app.model.Gas;
import com.example.app.model.Log;
import com.example.app.utils.Alerts;
import com.example.app.utils.FilteredBuildingBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.sql.Date;

public class AddGasController implements Alerts {
    private DBConn dbConn;
    private ArrayList<Building> buildings;
    @FXML
    private Label currentChargesError, meterReadError, fromBillingError,
            toBillingError, billedCCFError, buildingError;
    @FXML
    private TextField currentCharges, meterRead, billedCCF;
    @FXML
    private DatePicker fromBilling, toBilling;
    @FXML
    private ComboBox<Building> buildingComboBox;
    @FXML
    private Button uplaodGasCSVButton, downloadGasCSVButton, addGasButton;
    private FilteredBuildingBox buildingBox;
    private BuildingRecords buildingRecords;

    private Building building;
    private float cCharges, mRead, bCCF;
    private Date fBilling, tBilling;

    public AddGasController(){}

    public AddGasController(DBConn c){
        dbConn = c;
    }

    public void initialize() {
        if(dbConn == null){
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        fromBilling.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            try {
                if (!isNowFocused) {
                    fromBilling.setValue(fromBilling.getConverter()
                            .fromString(fromBilling.getEditor().getText()));
                }
            } catch (Exception e) {
                fromBilling.setValue(null);
            }
        });

        toBilling.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            try {
                if (!isNowFocused) {
                    toBilling.setValue(toBilling.getConverter()
                            .fromString(toBilling.getEditor().getText()));
                }
            } catch (Exception e) {
                toBilling.setValue(null);
            }
        });
    }

    public void clearGasErrors(){
        currentChargesError.setText(null);
        meterReadError.setText(null);
        fromBillingError.setText(null);
        toBillingError.setText(null);
        billedCCFError.setText(null);
    }

    public void clearGasInputs(){
        currentCharges.setText(null);
        meterRead.setText(null);
        fromBilling.setValue(null);
        toBilling.setValue(null);
        billedCCF.setText(null);
    }
    public void clearStored(){
        cCharges = 0;
        mRead = 0;
        fBilling = null;
        tBilling = null;
        bCCF = 0;
    }

    public boolean validity(){
        clearStored();

        boolean valid = true;

        if(buildingComboBox.getValue() == null){
            buildingError.setText("ERROR: building must be selected");
            valid = false;
        }
        else{
            building = buildingComboBox.getValue();
        }

        if(currentCharges.getText() == null || currentCharges.getText().isEmpty()){
            currentChargesError.setText("ERROR: Current Charges can't be nothing");
            valid = false;
        }else{
            try{
                cCharges = Float.parseFloat(currentCharges.getText());
                if(cCharges < 0){
                    currentChargesError.setText("ERROR: Current Charges can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                currentChargesError.setText("ERROR: Current Charges must be a number");
                valid = false;
            }
        }

        if(meterRead.getText() == null || meterRead.getText().isEmpty()){
            meterReadError.setText("ERROR: Meter Read can't be nothing");
            valid = false;
        }else{
            try{
                mRead = Float.parseFloat(meterRead.getText());
                if(mRead < 0){
                    meterReadError.setText("Meter Read can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                meterReadError.setText("ERROR: Meter Read must be a number");
                valid = false;
            }
        }

        if(billedCCF.getText() == null || billedCCF.getText().isEmpty()){
            billedCCFError.setText("ERROR: Billed CCF can't be nothing");
            valid = false;
        }else{
            try{
                bCCF = Float.parseFloat(billedCCF.getText());
                if(bCCF < 0){
                    billedCCFError.setText("ERROR: Billed CCF can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                billedCCFError.setText("ERROR: Billed CCF must be a number");
                valid = false;
            }
        }

        if(fromBilling.getValue() == null){
            fromBillingError.setText("ERROR: From Billing can't be nothing");
            valid = false;
        }

        if(toBilling.getValue() == null){
            toBillingError.setText("ERROR: To Billing can't be nothing");
        }

        // if the input text is empty set from billing to null
        if(fromBilling.getEditor().getText() == null){
            fBilling = null;
            fromBilling.setValue(null);
        }else {
            fBilling = Date.valueOf(fromBilling.getValue());
        }

        // if the input text is empty set to billing to null
        if(toBilling.getEditor().getText() == null){
            tBilling = null;
            toBilling.setValue(null);
        }else {
            tBilling = Date.valueOf(toBilling.getValue());
        }

        if(fromBilling.getValue() != null && toBilling.getValue() != null){
            if(fBilling.after(tBilling)){
                fromBillingError.setText("ERROR: From Billing can't be after To Billing");
                toBillingError.setText("ERROR: To Billing can't be before From Billing");
                valid = false;
            }
            if(fBilling.equals(tBilling)){
                fromBillingError.setText("ERROR: From Billing and To Billing can't be the same value");
                valid = false;
            }
        }

        return valid;
    }

    public void addGas(){
        clearGasErrors();
        if(validity()){
            Gas gas = new Gas();
            gas.setBuildingID(building.getBuildingID());
            gas.setCurrentCharges(cCharges);
//            gas.setRate("NEED TO REMOVE THIS");
            gas.setFromBilling(fBilling);
            gas.setToBilling(tBilling);
            gas.setMeterRead(mRead);
            gas.setBilledCCF(bCCF);

            GasRecords gasRecords = new GasRecords(dbConn);
            boolean success = gasRecords.insertGas(gas);

            if(gasRecords.findGas(toBilling.getValue().getYear(), toBilling.getValue().getMonthValue(), gas.getBuildingID())){
                alreadyExists();
            }else {
                if (success) {
                    // Log inserted data here
                    LogRecords logRecords = new LogRecords(dbConn);
                    Log log = new Log();
                    log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
                    log.setEvent(String.format("Inserted new gas record for building ID %d (from %s to %s, charges = %.2f).",
                            gas.getBuildingID(), gas.getFromBilling(), gas.getToBilling(), gas.getCurrentCharges()));
                    logRecords.insertLog(log);
                    insertSuccessful();
                    clearGasInputs();
                    clearStored();
                } else {
                    insertFail();
                }
            }
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

            LogRecords logRecords = new LogRecords(dbConn);
            Log log = new Log();
            log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
            log.setEvent("CSV was uploaded for Gas.");
            logRecords.insertLog(log);

//            System.out.println("CSV Upload Complete.");
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
}
