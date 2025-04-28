package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.MasterMeterLogic;
import com.example.app.dao.LogRecords;
import com.example.app.model.Building;
import com.example.app.model.Log;
import com.example.app.utils.Alerts;
import com.example.app.utils.FilteredBuildingBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class UpdateBuildingController implements Alerts {
    private DBConn dbConn;
    ArrayList<Building> buildings;
    @FXML
    private Label nameError, locationError, squareFeetError, constructionDateError, startSharedError, endSharedError;
    @FXML
    private TextField buildingLocation, squareFeet;
    @FXML
    private DatePicker constructionDate, startShared, endShared;
    @FXML
    private ComboBox<Building> buildingComboBox;
    private String loc;
    private int sqft;
    private Date cDate, sShared, eShared;
    private BuildingRecords buildingRecords;
    private FilteredBuildingBox buildingBox;



    public UpdateBuildingController(){}

    public UpdateBuildingController(DBConn c){
        dbConn = c;
    }

    public void initialize() {
        if(dbConn == null){
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        buildingComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            onChange();
        });

        constructionDate.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                constructionDate.setValue(constructionDate.getConverter()
                        .fromString(constructionDate.getEditor().getText()));
            }
        });

        startShared.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                startShared.setValue(startShared.getConverter()
                        .fromString(startShared.getEditor().getText()));
            }
        });

        endShared.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                endShared.setValue(endShared.getConverter()
                        .fromString(endShared.getEditor().getText()));
            }
        });
    }

    public void clearErrors(){
        nameError.setText(null);
        locationError.setText(null);
        squareFeetError.setText(null);
        constructionDateError.setText(null);
        startSharedError.setText(null);
        endSharedError.setText(null);
    }

    public void clearInputs(){
        buildingLocation.setText(null);
        squareFeet.setText(null);
        constructionDate.setValue(null);
        startShared.setValue(null);
        endShared.setValue(null);
    }

    public void setDisableInput(boolean d){
        buildingLocation.setDisable(d);
        squareFeet.setDisable(d);
        constructionDate.setDisable(d);
        startShared.setDisable(d);
        endShared.setDisable(d);
    }

    public void clearStored(){
        loc = null;
        sqft = 0;
        cDate = null;
        sShared = null;
        eShared = null;
    }

    public boolean validity(){
        clearStored();

        boolean valid = true;

        if(buildingComboBox.getValue() == null){
            nameError.setText("ERROR: Must have a building selected");
            valid = false;
        }

        if(startShared.getValue() != null){
            if(startShared.getEditor().getText() == null){
                sShared = null;
                startShared.setValue(null);
            }
            else {
                sShared = Date.valueOf(startShared.getValue());
                if (squareFeet.getText() == null) {
                    squareFeetError.setText("ERROR: Square Feet can't be nothing if building is on the master meter");
                    valid = false;
                }
            }
        }

        if(startShared.getValue() == null){
            if(endShared.getValue() != null){
                startSharedError.setText("ERROR: Missing date here");
                valid = false;
            }
        }

        if(startShared.getValue() != null && endShared.getValue() != null){
            if(endShared.getEditor().getText() == null){
                eShared = null;
                endShared.setValue(null);
            }else {
                eShared = Date.valueOf(endShared.getValue());
                if (eShared.before(sShared)) {
                    endSharedError.setText("ERROR: building can't be removed from master meter before it is added");
                    valid = false;
                } else if (eShared.equals(sShared)) {
                    startSharedError.setText("ERROR: Building can't be added to master meter and removed from it on the same day");
                    valid = false;
                }
            }
        }

        if(constructionDate.getValue() == null){
            constructionDateError.setText("ERROR: Construction Date can't be empty");
            valid = false;
        }else{
            cDate = Date.valueOf(constructionDate.getValue());
            if(sShared != null && sShared.before(cDate)){
                startSharedError.setText("ERROR: Building can't be on master meter before Construction Date");
                valid = false;
            }
        }

        if(squareFeet.getText() == null || squareFeet.getText().isEmpty()){
            sqft = -1;
        }else {
            if (squareFeet.getText() != null) {
                try {
                    sqft = Integer.parseInt(squareFeet.getText());
                    if(sqft < 0){
                        squareFeetError.setText("ERROR: Square Feet can't be negative");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    squareFeetError.setText("ERROR: Square Feet must be a number");
                    valid = false;
                }
            }
        }

        loc = buildingLocation.getText();

        return valid;
    }

    public void updateBuildings(){
        clearErrors();
        if(validity()){
            Building building = new Building();

            building.setBuildingID(buildingComboBox.getValue().getBuildingID());
            building.setLocation(buildingLocation.getText());
            if(squareFeet.getText() == null){
                building.setSqFT(-1);
            }
            else if(squareFeet.getText() == null || squareFeet.getText().isEmpty()){
                building.setSqFT(-1);
            }else{
                building.setSqFT(Integer.parseInt(squareFeet.getText()));
            }
            if(constructionDate.getValue() != null) {
                building.setDate(Date.valueOf(constructionDate.getValue()));
            }
            if(startShared.getValue() != null) {
                building.setStartShared(Date.valueOf(startShared.getValue()));
            }
            if(endShared.getValue() != null) {
                building.setEndShared(Date.valueOf(endShared.getValue()));
            }

            boolean success = buildingRecords.updateBuilding(building);
            if(success){
                boolean updatingMasterMeter = false;
                Building selectedBuilding = buildingComboBox.getValue();
                if(selectedBuilding.getSqFT() != sqft || selectedBuilding.getStartShared() != sShared){
                    updatingMasterMeter = true;
                    MasterMeterLogic masterMeterLogic = new MasterMeterLogic(dbConn, false);
                    masterMeterLogic.updateAllFrom(new java.sql.Date(selectedBuilding.getStartShared().getTime()));
                }
                buildingRecords = new BuildingRecords(dbConn);
                buildings = buildingRecords.getBuildings();
                buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);
                buildingComboBox.setValue(null);
                buildingComboBox.hide();
                if(!updatingMasterMeter) {
                    Platform.runLater(() -> {
                        updateSuccessful();
                    });
                }

                LogRecords logRecords = new LogRecords(dbConn);
                Log log = new Log();
                log.setTimestamp(new java.sql.Date(System.currentTimeMillis()));
                log.setEvent("Building `" + building.getName() + "` was updated");
                logRecords.insertLog(log);
                clearStored();
            }else{
                updateFail();
            }
        }
    }

    public void onChange(){
        clearErrors();
        if(buildingComboBox.getValue() != null){
            setDisableInput(false);
            Building building = buildingComboBox.getValue();
            buildingLocation.setText(building.getLocation());
            if(building.getSqFT() == 0){
                squareFeet.setText(null);
            }else{
                squareFeet.setText(String.format("%d", building.getSqFT()));
            }
            if(building.getDate() == null){
                constructionDate.setValue(null);
            }else {
                //convert from Date to String to LocalDate. This is the quickest way I could make it work
                constructionDate.setValue(LocalDate.parse(building.getDate().toString()));
            }
            if(building.getStartShared() == null){
                startShared.setValue(null);
            }else {
                //convert from Date to String to LocalDate. This is the quickest way I could make it work
                startShared.setValue(LocalDate.parse(building.getStartShared().toString()));
            }
            if(building.getEndShared() == null){
                endShared.setValue(null);
            }else{
                //convert from Date to String to LocalDate. This is the quickest way I could make it work
                endShared.setValue(LocalDate.parse(building.getEndShared().toString()));
            }
        }else{
            clearInputs();
            setDisableInput(true);
        }
    }
}
