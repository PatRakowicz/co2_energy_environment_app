package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.model.Building;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class AddDataController extends ApplicationController{
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

    float eUsage;
    float eCost;
    float wUsage;
    float wCost;
    float sCost;
    float mCost;
    LocalDate date;
    Object building;


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
            building = buildingComboBox.getValue();
        }


        return valid;
    }

    public void add(){
        clearErrors();
        if(validity()){
            //add data to database

            clearInputs();
        }

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

        ObservableList<Building> oBuildings = FXCollections.observableArrayList(buildings);
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
    }

    public void onChange(){
        if(buildingComboBox.getValue() != null && datePicker.getValue() != null) {
            String name = buildingComboBox.getValue().getName();
            Date checkDate = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            System.out.println("name = " + name);
            System.out.println("date = " + date);

            for (int i = 0; i < buildings.size(); i++) {
                String checkName = buildings.get(i).getName();


                if (Objects.equals(checkName, name)) {

                    System.out.println("checkName = " + checkName);

                    Date checkDateStart = buildings.get(i).getStartShared();
                    Date checkDateEnd = buildings.get(i).getEndShared();


                    System.out.println("checkDateStart = " + checkDateStart);
                    System.out.println("checkDateEnd = " + checkDateEnd);
                    if(checkDateStart != null) {
                        if (checkDateStart.before(checkDate)) {
                            if (checkDateEnd == null) {
                                electricityCost.setDisable(true);
                                electricityUsage.setDisable(true);
                            } else if (checkDateEnd.after(checkDate)) {
                                electricityCost.setDisable(true);
                                electricityUsage.setDisable(true);
                            } else {
                                electricityUsage.setDisable(false);
                                electricityCost.setDisable(false);
                            }

                        } else {
                            electricityUsage.setDisable(false);
                            electricityCost.setDisable(false);
                        }
                    }
                    break;
                }
            }
        }
    }
}
