package com.example.app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
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

    public ApplicationController(Stage newStage){
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/home-view.fxml"));
            scene = new Scene(root);
            newStage.setTitle("home");
            newStage.setScene(scene);
            newStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }

        //this makes sure the database connection is verified when the application is launched
        Button button = (Button) scene.lookup("#DBButton");
        if(checkDatabaseConnection()){
            connected = true;
            button.setStyle("-fx-background-color: LimeGreen");
        }
        else{
            connected = false;
            button.setStyle("-fx-background-color: #f74545");
        }


        //get building info and save to buildings ArrayList
    }

    public Boolean checkDatabaseConnection(){
        // add the code here to check if the database is connected or not


        return true;
    }

    @FXML
    public void switchToAddData(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/add-data-view.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("/fxml/prediction-view.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("/fxml/report-view.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("/fxml/scenarios-view.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("/fxml/update-data-view.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("/fxml/view-data-view.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("/fxml/home-view.fxml"));
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
            stage.setMinHeight(600);
            stage.setMinWidth(400);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
