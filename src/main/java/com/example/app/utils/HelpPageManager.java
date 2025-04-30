package com.example.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.example.app.controllers.ApplicationController.helpStage;

public interface HelpPageManager {
    default void openHelpPage(){
        helpStage.show();
    }

    default void closeHelpPage(){
        helpStage.hide();
    }

    default void setHelpPage(String page){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            helpStage.setScene(scene);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
