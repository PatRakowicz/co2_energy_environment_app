package com.example.app.utils;

import com.example.app.controllers.*;
import com.example.app.dao.DBConn;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {
    public static void setScene(Stage stage, String fxml, DBConn dbConn, String title) {
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

            controller.setDbController(dbConn);
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
