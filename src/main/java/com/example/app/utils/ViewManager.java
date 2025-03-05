package com.example.app.utils;

import com.example.app.controllers.ApplicationController;
import com.example.app.controllers.DBController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewManager {
    public static void setScene(Stage stage, String fxml, DBController dbController, String title) {
        try {
            FXMLLoader fmxlLoader = new FXMLLoader(ViewManager.class.getResource(fxml));
            Parent root = fmxlLoader.load();

            ApplicationController controller = fmxlLoader.getController();
            controller.setDbController(dbController);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            stage.setMinHeight(400);
            stage.setMinWidth(600);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Caught Error: %s", e);
        }

    }

}
