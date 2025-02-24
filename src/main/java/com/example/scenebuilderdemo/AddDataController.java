package com.example.scenebuilderdemo;

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
    private Label errorOutput;
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


    public void add(ActionEvent event){
        try {
            errorOutput.setText("");
            eUsage = Integer.parseInt(electricityUsage.getText());
            eCost = Float.parseFloat(electricityCost.getText());
            wUsage = Integer.parseInt(waterUsage.getText());
            wCost = Float.parseFloat(waterCost.getText());
            sCost = Float.parseFloat(sewageCost.getText());
            mCost = Float.parseFloat(miscCost.getText());
            date = datePicker.getValue();


        } catch(NumberFormatException e){
            errorOutput.setText("ERROR: Inputs must be numbers");
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}
