package com.example.scenebuilderdemo;

import javafx.beans.binding.BooleanBinding;
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

public class UpdateDataController {
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
    private Button updateButton;
    @FXML
    private DatePicker datePicker;

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

}
