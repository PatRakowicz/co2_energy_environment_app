package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.LogRecords;
import com.example.app.dao.UpdateGasLogic;
import com.example.app.model.Building;
import com.example.app.model.Gas;
import com.example.app.model.Log;
import com.example.app.utils.Alerts;
import com.example.app.utils.FilteredBuildingBox;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class UpdateGasController implements Alerts {
    private DBConn dbConn;
    private ArrayList<Building> buildings;
    private BuildingRecords buildingRecords;
    private FilteredBuildingBox buildingBox;
    private UpdateGasLogic updateGasLogic;
    @FXML
    private ComboBox<Building> buildingComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private TextField currentCharges, meterRead, billedCCF;
    @FXML
    private DatePicker fromBilling, toBilling;
    @FXML
    private Label buildingError, yearError, monthError, currentChargesError, meterReadError, fromBillingError,
            toBillingError, billedCCFError;
    private Building building;
    private float cCharges, mRead, bCCF;
    private Date fBilling, tBilling;
    private Gas selectedGas;

    public UpdateGasController(){}

    public UpdateGasController(DBConn c){
        dbConn = c;
    }

    public void initialize() {
        if(dbConn == null){
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        updateGasLogic = new UpdateGasLogic(dbConn);

        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        int currentYear = LocalDate.now().getYear();
        int minYear = updateGasLogic.getMinGasYear();
        for (int year = currentYear; year >= minYear; year--) {
            yearComboBox.getItems().add(year);
        }

        ChangeListener valueChangeListener = (obs, oldValue, newValue) -> {
            onChange();
        };

        buildingComboBox.valueProperty().addListener(valueChangeListener);
        yearComboBox.valueProperty().addListener(valueChangeListener);
        monthComboBox.valueProperty().addListener(valueChangeListener);

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
        buildingError.setText(null);
        yearError.setText(null);
        monthError.setText(null);
    }

    public void clearGasInputs(){
        currentCharges.setText(null);
        meterRead.setText(null);
        fromBilling.setValue(null);
        toBilling.setValue(null);
        billedCCF.setText(null);
    }

    public void setInputDisabled(boolean d){
        currentCharges.setDisable(d);
        meterRead.setDisable(d);
        fromBilling.setDisable(d);
        toBilling.setDisable(d);
        billedCCF.setDisable(d);
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

    public void updateGas(){
        clearGasErrors();

        if(validity()){
            Gas gas = new Gas();
            gas.setGasID(selectedGas.getGasID());
            gas.setBuildingID(selectedGas.getBuildingID());
            gas.setCurrentCharges(cCharges);
            gas.setMeterRead(mRead);
            gas.setFromBilling(fBilling);
            gas.setToBilling(tBilling);
            gas.setBilledCCF(bCCF);

            Building building = buildingComboBox.getValue();
            boolean success = updateGasLogic.updateGas(building, gas);

            if (success){
                LogRecords logRecords = new LogRecords(dbConn);
                Log log = new Log();
                log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
                log.setEvent("Gas for `" + gas.getFromBilling() + ", " + gas.getToBilling() + "` was updated.");
                logRecords.insertLog(log);

                clearGasInputs();
                yearComboBox.setValue(null);
                monthComboBox.setValue(null);
                selectedGas = null;
                updateSuccessful();
            }
            else {
                updateFail();
            }

        }
    }

    public void deleteGas(){
        if(buildingComboBox.getValue() != null && yearComboBox.getValue() != null && !monthComboBox.getValue().isEmpty()){
            try {
                //load resource file into new stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/delete-confirmation.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.initStyle(StageStyle.UTILITY);

                Button yes = (Button) scene.lookup("#yes");
                Button no = (Button) scene.lookup("#no");

                yes.setOnAction(e -> {
                    boolean success = updateGasLogic.deleteGas(selectedGas);
                    if(success){

                        LogRecords logRecords = new LogRecords(dbConn);
                        Log log = new Log();
                        log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
                        log.setEvent("Gas for `" + selectedGas.getFromBilling() + ", " + selectedGas.getToBilling() + "` was deleted.");
                        logRecords.insertLog(log);

                        deleteSuccessful();
                        clearGasInputs();
                        yearComboBox.setValue(null);
                        monthComboBox.setValue(null);
                        selectedGas = null;
                        stage.close();
                    }else{
                        deleteFail();
                    }
                });

                no.setOnAction(e -> {
                    stage.close();
                });

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onChange(){
        clearGasErrors();
        if(buildingComboBox.getValue() != null && yearComboBox.getValue() != null && monthComboBox.getValue() != null){
            Building selectedBuilding = buildingComboBox.getValue();
            int selectedYear = yearComboBox.getValue();
            int selectedMonthIndex = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            clearGasInputs();

            Gas gas = updateGasLogic.getGasForDate(selectedBuilding.getBuildingID(), selectedYear, selectedMonthIndex);

            if (gas != null) {
                selectedGas = gas;
                currentCharges.setText(Float.toString(gas.getCurrentCharges()));
                meterRead.setText(Float.toString(gas.getMeterRead()));
                fromBilling.setValue(gas.getFromBilling().toLocalDate());
                toBilling.setValue(gas.getToBilling().toLocalDate());
                billedCCF.setText(Float.toString(gas.getBilledCCF()));
                setInputDisabled(false);
            } else {
                clearGasInputs();
                setInputDisabled(true);
            }
        }else{
            clearGasInputs();
            setInputDisabled(true);
        }
    }
}
