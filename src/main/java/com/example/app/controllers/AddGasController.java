package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.GasRecords;
import com.example.app.model.Building;
import com.example.app.model.Gas;
import com.example.app.utils.FilteredBuildingBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.sql.Date;

public class AddGasController {
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
        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);
    }

    public void setBuildings(ArrayList<Building> buildings) {
        buildingBox.setList(buildings);
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

    public boolean validity(){
        boolean valid = true;

        if(buildingComboBox.getValue() == null){
            buildingError.setText("ERROR: building must be selected");
            valid = false;
        }
        else{
            building = buildingComboBox.getValue();
        }

        if(currentCharges.getText().isEmpty()){
            currentChargesError.setText("ERROR: Current Charges can't be nothing");
            valid = false;
        }else{
            try{
                cCharges = Float.parseFloat(currentCharges.getText());
            } catch (NumberFormatException e) {
                currentChargesError.setText("ERROR: Current Charges must be a number");
                valid = false;
            }
        }

        if(meterRead.getText().isEmpty()){
            meterReadError.setText("ERROR: Meter Read can't be nothing");
            valid = false;
        }else{
            try{
                mRead = Float.parseFloat(meterRead.getText());
            } catch (NumberFormatException e) {
                meterReadError.setText("ERROR: Meter Read must be a number");
                valid = false;
            }
        }

        if(billedCCF.getText().isEmpty()){
            billedCCFError.setText("ERROR: Billed CCF can't be nothing");
            valid = false;
        }else{
            try{
                bCCF = Float.parseFloat(billedCCF.getText());
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

        if(fromBilling.getValue() != null && toBilling.getValue() != null){
            fBilling = Date.valueOf(fromBilling.getValue());
            tBilling = Date.valueOf(toBilling.getValue());
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
            gas.setCurrent_charges(cCharges);
            gas.setRate("NEED TO REMOVE THIS");
            gas.setFrom_billing(fBilling);
            gas.setTo_billing(tBilling);
            gas.setMeter_read(mRead);
            gas.setBilled_ccf(bCCF);

            GasRecords gasRecords = new GasRecords(dbConn);
            boolean success = gasRecords.insertGas(gas);

            if (success) {
                // Log inserted data here
                alertSuccsess();
                clearGasInputs();
            } else {
                alertFailure();
            }
        }
    }
    public void alertSuccsess(){
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setContentText("Data Inserted Successfully!");
        a.show();
    }

    public void alertFailure(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText("Failed to insert data \n Please try again or check database connection");
        a.show();
    }



    public void handleUploadGasCsv(){

    }

    public void handleDownloadGasCsvTemplate(){

    }
}
