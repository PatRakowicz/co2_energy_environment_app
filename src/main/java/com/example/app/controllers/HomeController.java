package com.example.app.controllers;

import com.example.app.controllers.database.DBController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HomeController extends ApplicationController{
    @FXML
    private Button DBButton;

    private DBController dbController;

    public void initialize() {
        dbController = new DBController();
        checkDBConnection();
    }

    private void checkDBConnection() {
        if (dbController.isConnectionSuccessful()) {
            DBButton.setStyle("-fx-background-color: LimeGreen");
        } else {
            DBButton.setStyle("-fx-background-color: #f74545");
        }
    }

    public void openDBWindow (){
        try {
            DBButton.setDisable(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBWindow.fxml"));
            DBRoot = loader.load();
            dbController = loader.getController();
            DBStage = new Stage();
            DBScene = new Scene(DBRoot);
            DBStage.initModality(Modality.APPLICATION_MODAL);
            DBStage.setScene(DBScene);
            DBStage.setResizable(false);
            DBStage.initStyle(StageStyle.UTILITY);

            DBStage.setOnHidden(windowEvent -> {
                DBButton.setDisable(false);
                checkDBConnection();
            });

            DBStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
