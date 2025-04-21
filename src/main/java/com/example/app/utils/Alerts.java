package com.example.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public interface Alerts {
    default void alert(String file){
        try {
            //load resource file into new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UTILITY);

            //if user clicks with mouse, close the stage
            root.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    stage.close();
                }
            });

            //if focus is lost close the stage
            stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    stage.close();
                }
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void insertSuccessful(){
        alert("/fxml/alert-insert-successful.fxml");
    }

    default void insertFail(){
        alert("/fxml/alert-insert-fail.fxml");
    }
}
