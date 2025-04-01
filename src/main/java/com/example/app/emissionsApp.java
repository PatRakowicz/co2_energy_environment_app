package com.example.app;

import com.example.app.controllers.ApplicationController;
import com.example.app.dao.DBConn;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class emissionsApp extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            DBConn dbConn = new DBConn();     // shared controller

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));

            ApplicationController applicationController = new ApplicationController();
            applicationController.setDbController(dbConn);
            fxmlLoader.setController(applicationController);

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Home");
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}