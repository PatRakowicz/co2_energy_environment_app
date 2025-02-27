package com.example.MainApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddDataController {
    private Stage stage;
    private Scene scene;
    private Parent root;
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
    private Button addButton;
    @FXML
    private DatePicker datePicker;

    int eUsage;
    float eCost;
    int wUsage;
    float wCost;
    float sCost;
    float mCost;
    LocalDate date;


    public void switchToHome(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    public void clearErrors(){
        electricityUsageError.setText("");
        electricityCostError.setText("");
        waterUsageError.setText("");
        waterCostError.setText("");
        sewageCostError.setText("");
        miscCostError.setText("");
    }


    public void clearInputs(){
        electricityUsage.setText("");
        electricityCost.setText("");
        waterUsage.setText("");
        waterCost.setText("");
        sewageCost.setText("");
        miscCost.setText("");
    }

    // This is where the error checking happens
    public boolean validity(){
        boolean valid = true;
        try {
            eUsage = Integer.parseInt(electricityUsage.getText());
        } catch (NumberFormatException ex) {
            if(electricityUsage.getText().isEmpty()){
                electricityUsageError.setText("ERROR: Electricity Usage can't be blank");
            }
            else {
                electricityUsageError.setText("ERROR: Electricity Usage must be whole number");
            }
            valid = false;
        }
        try {
            eCost = Float.parseFloat(electricityCost.getText());
        } catch (NumberFormatException e) {
            if(electricityCost.getText().isEmpty()){
                electricityCostError.setText("ERROR: Electricity Cost can't be blank");
            }
            else {
                electricityCostError.setText("ERROR: Electricity Cost must be a number");
            }
            valid = false;
        }
        try {
            wUsage = Integer.parseInt(waterUsage.getText());
        } catch (NumberFormatException e) {
            if(waterUsage.getText().isEmpty()){
                waterUsageError.setText("ERROR: Water Usage can't be blank");
            }
            else {
                waterUsageError.setText("ERROR: Water Usage must be a whole number");
            }
            valid = false;
        }
        try {
            wCost = Float.parseFloat(waterCost.getText());
        } catch (NumberFormatException e) {
            if(waterCost.getText().isEmpty()){
                waterCostError.setText("ERROR: Water Cost can't be blank");
            }
            else {
                waterCostError.setText("ERROR: Water Cost must be a number");
            }
            valid = false;
        }
        try {
            sCost = Float.parseFloat(sewageCost.getText());
        } catch (NumberFormatException e) {
            if(sewageCost.getText().isEmpty()){
                sewageCostError.setText("ERROR: Sewage Cost can't be blank");
            }
            else {
                sewageCostError.setText("ERROR: Sewage Cost must be a number");
            }
            valid = false;
        }
        try {
            mCost = Float.parseFloat(miscCost.getText());
        } catch (NumberFormatException e) {
            if(miscCost.getText().isEmpty()){
                miscCostError.setText("ERROR: Misc. Cost can't be blank");
            }
            else {
                miscCostError.setText("ERROR: Misc. Cost must be a number");
            }
            valid = false;
        }
        date = datePicker.getValue();



        return valid;
    }

    public void add(ActionEvent event){
        clearErrors();
        if(validity()){
            //add data to database

            clearInputs();
        }

    }
}
