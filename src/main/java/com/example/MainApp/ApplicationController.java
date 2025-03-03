package com.example.MainApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;

public class ApplicationController{
    protected Stage stage;
    protected Scene scene;
    protected Parent root;
    protected Parent DBRoot;
    protected Stage DBStage;
    protected Scene DBScene;
    protected boolean connected;
    protected ArrayList<String> buildings;

    public ApplicationController(){}

    public Boolean checkDatabaseConnection(){
        // add the code here to check if the database is connected or not


        return false;
    }

    @FXML
    public void switchToAddData(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("add-data-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("add data");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToPrediction(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("prediction-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("prediction");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToReport(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("report-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("reports");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToScenarios(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("scenarios-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("scenarios");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToUpdateData(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("update-data-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("update data");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToViewData(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("view-data-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("view data");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToHome(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            Button button = (Button) scene.lookup("#DBButton");
            if(connected){
                button.setStyle("-fx-background-color: LimeGreen");
            }else{
                button.setStyle("-fx-background-color: #f74545");
            }
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
