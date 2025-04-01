package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.model.Building;
import com.example.app.utils.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ApplicationController {
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    protected ArrayList<Building> buildings;

    @FXML
    private Button DBButton;
    protected DBController dbController;
    private Stage DBStage;

    public ApplicationController() {
//        dbController = new DBController();
        // We need to not create a new DBController by default
        // updated the emissionsApp to pass DBController
    }

//    public ApplicationController(Stage newStage) {
//        try {
//            root = FXMLLoader.load(getClass().getResource("/fxml/home-view.fxml"));
//            scene = new Scene(root);
//            newStage.setTitle("home");
//            newStage.setScene(scene);
//            newStage.setMinHeight(400);
//            newStage.setMinWidth(600);
//            newStage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    public void initialize() {
        updateDBButtonStatus();
    }

    public void setDbController(DBController dbController) {
        this.dbController = dbController;
    }

    private void updateDBButtonStatus() {
        if (dbController.isConnectionSuccessful()) {
            if (DBButton != null) {
                DBButton.setStyle("-fx-background-color: LimeGreen");
            }
        } else {
            if (DBButton != null) {
                DBButton.setStyle("-fx-background-color: #f74545");
            }
        }
    }

    public void openDBWindow() {
        try {
            if (DBStage != null && DBStage.isShowing()) {
                return;
            }

            DBButton.setDisable(true);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBWindow.fxml"));
            Parent DBRoot = loader.load();
            Scene DBScene = new Scene(DBRoot);
            DBStage = new Stage();
            DBStage.initModality(Modality.APPLICATION_MODAL);
            DBStage.setScene(DBScene);
            DBStage.setResizable(false);
            DBStage.initStyle(StageStyle.UTILITY);

            DBStage.setOnHidden(windowEvent -> {
                DBButton.setDisable(false);
                updateDBButtonStatus();
            });

            DBStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToAddData(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/add-data-view.fxml", dbController, "Dashboard");
    }

    @FXML
    public void switchToPrediction(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/prediction-view.fxml", dbController, "Dashboard");
    }

    @FXML
    public void switchToReport(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/report-view.fxml", dbController, "Dashboard");
    }

    @FXML
    public void switchToScenarios(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/scenarios-view.fxml", dbController, "Dashboard");
    }

    @FXML
    public void switchToUpdateData(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/update-data-view.fxml", dbController, "Dashboard");
    }

    @FXML
    public void switchToViewData(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/view-data-view.fxml", dbController, "Dashboard");
    }

    @FXML
    public void switchToHome(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ViewManager.setScene(stage, "/fxml/home-view.fxml", dbController, "Dashboard");
    }

}
