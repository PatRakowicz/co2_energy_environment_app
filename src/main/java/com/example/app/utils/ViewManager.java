package com.example.app.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class ViewManager {
    public static void setScene(ActionEvent event, String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(ViewManager.class.getResource(fxml)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinHeight(600);
            stage.setMinWidth(400);
            stage.show();
        } catch (Exception e) {
            System.out.printf("Caught Error: %s", e);
        }

    }

}
