package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDate;


public class UpdateDataController extends ApplicationController{
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
    private ChoiceBox buildingChoice;

    float eUsage;
    float eCost;
    float wUsage;
    float wCost;
    float sCost;
    float mCost;
    LocalDate date;
    String building;


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

        if(buildingChoice.getValue() == null){
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


    /* trying to disable the buttons and textfields until a building and date are chosen,
        but haven't quite figured it out yet

    public void onChange(){
        if(datePicker.getValue() != null){ // && buildings.getValue() != null

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
    */
}
