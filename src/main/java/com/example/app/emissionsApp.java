package com.example.app;

import com.example.app.utils.HelpPageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class emissionsApp extends javafx.application.Application implements HelpPageManager {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 650, 400);
            stage.setScene(scene);

            stage.setOnCloseRequest(event -> {
                closeHelpPage();
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