package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.model.Building;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;


public class UpdateDataController extends ApplicationController{
    ObservableList<Building> oBuildings;
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
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    private TextField editor;

    float eUsage;
    float eCost;
    float wUsage;
    float wCost;
    float sCost;
    float mCost;
    LocalDate date;
    Object building;
    Building lastNotNull;


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
            buildingError.setText("ERROR: building can't be nothing");
            valid = false;
        }
        else{
            //get value from buildingChoice and store it in building
        }


        return valid;
    }

    public void update(){
        clearErrors();
        if(validity()){
            //add data to database

            clearInputs();
        }

    }


    public void delete(){
        //delete data
    }

    @Override
    public void initialize() {
        super.initialize();

        if (dbController == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(super.dbController);
        buildings = buildingRecords.getBuildings();

        oBuildings = FXCollections.observableArrayList(buildings);
        buildingComboBox.setItems(oBuildings);

        buildingComboBox.setConverter(new StringConverter<Building>() {
            @Override
            public String toString(Building building) {
                if (building == null) {
                    return "";
                }
                return building.getName();
            }

            @Override
            public Building fromString(String s) {
                return null;
            }
        });

        editor = buildingComboBox.getEditor();

        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> filter());
        });

        editor.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            Platform.runLater(() -> lostFocus(isNowFocused));
        });
    }

    public void onChange(){
        if(buildingComboBox.getValue() != null){
            lastNotNull = buildingComboBox.getValue();
        }
        if(datePicker.getValue() != null && buildingComboBox.getValue() != null){

            //get info from database and populate values

            electricityUsage.setDisable(false);
            electricityCost.setDisable(false);
            waterUsage.setDisable(false);
            waterCost.setDisable(false);
            sewageCost.setDisable(false);
            miscCost.setDisable(false);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        }
        else{
            electricityUsage.setDisable(true);
            electricityCost.setDisable(true);
            waterUsage.setDisable(true);
            waterCost.setDisable(true);
            sewageCost.setDisable(true);
            miscCost.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        }
    }

    public void filter() {
        int caretPosition = editor.getCaretPosition();
        String input = editor.getText();
        ObservableList<Building> filteredList = FXCollections.observableArrayList();

        boolean buildingSelected = false;
        for (Building b : oBuildings) {
            if (b.getName().equals(input)) {
                buildingSelected = true;
            }
        }

        if (!buildingSelected) {
            buildingComboBox.show();
            if (input.isEmpty()) {
                buildingComboBox.setItems(oBuildings);
            } else {
                for (Building item : oBuildings) {
                    if (item.getName().toLowerCase().contains(input.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                buildingComboBox.setItems(filteredList);
            }
        }
        editor.positionCaret(caretPosition);
    }

    public void lostFocus(Boolean isNowFocused){
        if(buildingComboBox.getValue() == null && lastNotNull != null && !isNowFocused){
            buildingComboBox.setValue(lastNotNull);
            buildingComboBox.hide();
        }
    }
}
