package com.example.app.utils;

import com.example.app.controllers.*;
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
            FXMLLoader fxmlLoader = new FXMLLoader(ViewManager.class.getResource(fxml));

            ApplicationController controller = null;
            switch (fxml) {
                case "/fxml/home-view.fxml" -> controller = new ApplicationController();
                case "/fxml/add-data-view.fxml" -> controller = new AddDataController();
                case "/fxml/update-data-view.fxml" -> controller = new UpdateDataController();
                case "/fxml/view-data-view.fxml" -> controller = new ViewDataController();
                default -> new ApplicationController();
            }

            controller.setDbController(dbController);
            fxmlLoader.setController(controller);

            Parent root = fxmlLoader.load();

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
