package com.example.app.controllers;

import com.example.app.controllers.database.DBController;
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

import java.util.ArrayList;

public class ApplicationController {
    protected Stage stage;
    protected Scene scene;
    protected Parent root;
    protected ArrayList<String> buildings;

    @FXML
    private Button DBButton;
    private DBController dbController;
    private Stage DBStage;

    public ApplicationController() {
        dbController = new DBController();
    }

    public ApplicationController(Stage newStage) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/home-view.fxml"));
            scene = new Scene(root);
            newStage.setTitle("home");
            newStage.setScene(scene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //get building info and save to buildings ArrayList
    }

    @FXML
    public void initialize() {
        updateDBButtonStatus();
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
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/add-data-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("add data");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToPrediction(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/prediction-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("prediction");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToReport(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/report-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("reports");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToScenarios(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/scenarios-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("scenarios");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToUpdateData(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/update-data-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("update data");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToViewData(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/view-data-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("view data");

            // Get current buildings and send them to AddDataController

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToHome(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/home-view.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.setMinHeight(600);
            stage.setMinWidth(400);
            stage.show();

            updateDBButtonStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
