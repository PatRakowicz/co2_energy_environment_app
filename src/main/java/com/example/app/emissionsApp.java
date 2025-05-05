package com.example.app;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

import static com.example.app.controllers.ApplicationController.helpPageManager;

public class emissionsApp extends javafx.application.Application{
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Image image = new Image(getClass().getResource("/images/app_logo.jpg").toExternalForm());
            stage.getIcons().add(image);
            stage.setTitle("WCU Emissions");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 650, 400);
            stage.setScene(scene);

            stage.setOnCloseRequest(event -> {
                if(helpPageManager.getHelpStage() != null) {
                    helpPageManager.closeHelpPage();
                }
            });

            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}