package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.LogRecords;
import com.example.app.dao.UpdateUtilityLogic;
import com.example.app.model.Building;
import com.example.app.model.Log;
import com.example.app.model.Utility;
import com.example.app.utils.Alerts;
import com.example.app.utils.FilteredBuildingBox;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.sql.Date;

public class UpdateUtilityController implements Alerts {
    private DBConn dbConn;
    private BuildingRecords buildingRecords;
    private ArrayList<Building> buildings;
    private UpdateUtilityLogic updateUtilityLogic;
    private FilteredBuildingBox buildingBox;

    @FXML
    private Label electricityUsageError, electricityCostError, waterUsageError,
            waterCostError, sewageCostError, miscCostError, dateError, buildingError;
    @FXML
    private TextField electricityUsage, electricityCost, waterUsage, waterCost, sewageCost, miscCost;
    @FXML
    private ComboBox<Building> buildingComboBox;
    @FXML
    private Button updateUtilityButton, deleteUtilityButton;
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;

    private float eUsage, eCost, wUsage, wCost, sCost, mCost;
    private Date date;
    private Utility selectedUtility;

    public UpdateUtilityController(){}

    public UpdateUtilityController(DBConn c){
        dbConn = c;
    }

    public void initialize(){
        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        updateUtilityLogic = new UpdateUtilityLogic(dbConn);

        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        int currentYear = LocalDate.now().getYear();
        int minYear = updateUtilityLogic.getMinUtilityYear();
        for (int year = currentYear; year >= minYear; year--) {
            yearComboBox.getItems().add(year);
        }

        ChangeListener valueChangeListener = (obs, oldValue, newValue) -> {
            Platform.runLater(() -> onChange());
        };
        buildingComboBox.valueProperty().addListener(valueChangeListener);
        yearComboBox.valueProperty().addListener(valueChangeListener);
        monthComboBox.valueProperty().addListener(valueChangeListener);
    }

    public void onChange() {
        if(buildingComboBox.getValue() != null && yearComboBox.getValue() != null && monthComboBox.getValue() != null) {
            Building selectedBuilding = buildingComboBox.getValue();
            int selectedYear = yearComboBox.getValue();
            int selectedMonthIndex = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            clearInputs();


            Utility utility = updateUtilityLogic.getUtilityForDate(selectedBuilding.getBuildingID(), selectedYear, selectedMonthIndex);

            if (utility != null) {
                selectedUtility = utility;
                if(utility.getElectricityUsage() < 0){
                    electricityUsage.setText(null);
                }else {
                    electricityUsage.setText(String.valueOf(utility.getElectricityUsage()));
                }

                if(utility.getElectricityCost() < 0){
                    electricityCost.setText(null);
                }else {
                    electricityCost.setText(String.valueOf(utility.getElectricityCost()));
                }

                if(utility.getWaterUsage() < 0){
                    waterUsage.setText(null);
                }else {
                    waterUsage.setText(String.valueOf(utility.getWaterUsage()));
                }

                if(utility.getWaterCost() < 0){
                    waterCost.setText(null);
                }else {
                    waterCost.setText(String.valueOf(utility.getWaterCost()));
                }

                if(utility.getSewageCost() < 0){
                    sewageCost.setText(null);
                }else {
                    sewageCost.setText(String.valueOf(utility.getSewageCost()));
                }

                if(utility.getMiscCost() < 0){
                    miscCost.setText(null);
                }else {
                    miscCost.setText(String.valueOf(utility.getMiscCost()));
                }
                setInputDisabled(false);
            } else {
                clearInputs();
                setInputDisabled(true);
            }
        }else{
            clearInputs();
            setDisabledAll(true);
        }
    }

    public void updateUtility() {
        clearErrors();

        if (utilityValidity()) {
            Utility utility = new Utility();
            utility.setUtilityID(selectedUtility.getUtilityID());
            utility.setDate(date);
            utility.setElectricityUsage(eUsage);
            utility.setElectricityCost(eCost);
            utility.setWaterUsage(wUsage);
            utility.setWaterCost(wCost);
            utility.setSewageCost(sCost);
            utility.setMiscCost(mCost);

            Building building = buildingComboBox.getValue();
            boolean success = updateUtilityLogic.updateUtility(building, utility);

            if (success){

                LogRecords logRecords = new LogRecords(dbConn);
                Log log = new Log();
                log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
                log.setEvent("Utility for `" + monthComboBox + ", " + yearComboBox + "` was updated.");
                logRecords.insertLog(log);

                clearInputs();
                yearComboBox.setValue(null);
                monthComboBox.setValue(null);
                selectedUtility = null;
                updateSuccessful();
            }
            else {
                updateFail();
            }
        }
    }

    public void deleteUtility() {
        if(buildingComboBox.getValue() != null && yearComboBox.getValue() != null && monthComboBox.getValue() != null){
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
                    boolean success = updateUtilityLogic.deleteUtility(selectedUtility);
                    if(success){

                        LogRecords logRecords = new LogRecords(dbConn);
                        Log log = new Log();
                        log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
                        log.setEvent("Utility for `" + monthComboBox + ", " + yearComboBox + "` was deleted.");
                        logRecords.insertLog(log);

                        deleteSuccessful();
                        clearInputs();
                        yearComboBox.setValue(null);
                        monthComboBox.setValue(null);
                        selectedUtility = null;
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

    private boolean utilityValidity() {
        boolean valid = true;

        if(electricityUsage.getText() == null || electricityUsage.getText().isEmpty()){
            eUsage = -1;
        }else {
            try {
                eUsage = Float.parseFloat(electricityUsage.getText());
                if(eUsage < 0){
                    electricityUsageError.setText("ERROR: Electricity Usage can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException ex) {
                if (electricityUsage.getText() != null) {
                    electricityUsageError.setText("ERROR: Electricity Usage must be a number");
                    valid = false;
                }
            }
        }

        if(electricityCost.getText() == null || electricityCost.getText().isEmpty()){
            eCost = -1;
        }else {
            try {
                eCost = Float.parseFloat(electricityCost.getText());
                if(eCost < 0){
                    electricityCostError.setText("ERROR: Electricity Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (electricityCost.getText() != null) {
                    electricityCostError.setText("ERROR: Electricity Cost must be a number");
                    valid = false;
                }
            }
        }

        if(waterUsage.getText() == null || waterUsage.getText().isEmpty()){
            wUsage = -1;
        }else {
            try {
                wUsage = Float.parseFloat(waterUsage.getText());
                if(wUsage < 0){
                    waterUsageError.setText("ERROR: Water Usage can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (waterUsage.getText() != null) {
                    waterUsageError.setText("ERROR: Water Usage must be a number");
                    valid = false;
                }
            }
        }

        if(waterCost.getText() == null || waterCost.getText().isEmpty()){
            wCost = -1;
        }else {
            try {
                wCost = Float.parseFloat(waterCost.getText());
                if(wCost < 0){
                    waterCostError.setText("ERROR: Water Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (waterCost.getText() != null) {
                    waterCostError.setText("ERROR: Water Cost must be a number");
                    valid = false;
                }
            }
        }

        if(sewageCost.getText() == null || sewageCost.getText().isEmpty()){
            sCost = -1;
        }else {
            try {
                sCost = Float.parseFloat(sewageCost.getText());
                if(sCost < 0){
                    sewageCostError.setText("ERROR: Sewage Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (sewageCost.getText() != null) {
                    sewageCostError.setText("ERROR: Sewage Cost must be a number");
                    valid = false;
                }
            }
        }

        if(miscCost.getText() == null || miscCost.getText().isEmpty()){
            mCost = -1;
        }else {
            try {
                mCost = Float.parseFloat(miscCost.getText());
                if(mCost < 0){
                    miscCostError.setText("ERROR: Misc. Cost can't be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                if (miscCost.getText() != null) {
                    miscCostError.setText("ERROR: Misc. Cost must be a number");
                    valid = false;
                }
            }
        }

        if (yearComboBox.getValue() == null || monthComboBox.getValue() == null) {
            dateError.setText("ERROR: Month and Year must be selected.");
            valid = false;
        } else {
            int year = yearComboBox.getValue();
            int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            date = Date.valueOf(LocalDate.of(year, month, 1));
        }

        if (buildingComboBox.getValue() == null) {
            buildingError.setText("ERROR: Building must be selected");
            valid = false;
        }

        return valid;
    }

    private void clearInputs() {
        electricityUsage.setText("");
        electricityCost.setText("");
        waterUsage.setText("");
        waterCost.setText("");
        sewageCost.setText("");
        miscCost.setText("");
    }

    private void clearErrors() {
        electricityUsageError.setText("");
        electricityCostError.setText("");
        waterUsageError.setText("");
        waterCostError.setText("");
        sewageCostError.setText("");
        miscCostError.setText("");
        dateError.setText("");
        buildingError.setText("");
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

    private void setInputDisabled(boolean disabled) {
        if(!disabled) {
            if (buildingComboBox.getValue().getName().equals("Master Meter")) {
                electricityUsage.setDisable(false);
                electricityCost.setDisable(false);
                miscCost.setDisable(false);
                waterUsage.setDisable(true);
                waterUsage.setText(null);
                waterCost.setDisable(true);
                waterCost.setText(null);
                sewageCost.setDisable(true);
                sewageCost.setText(null);
            } else {
                // get the selected date
                int year = yearComboBox.getValue();
                int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
                LocalDate getDate = LocalDate.of(year, month, 1);
                java.util.Date checkDate = java.util.Date.from(getDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                java.util.Date checkDateStart = buildingComboBox.getValue().getStartShared();
                java.util.Date checkDateEnd = buildingComboBox.getValue().getEndShared();

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
        else {
            setDisabledAll(true);
        }
    }
}
