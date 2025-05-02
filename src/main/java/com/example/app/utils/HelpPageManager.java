package com.example.app.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.example.app.controllers.ApplicationController.help;


public class HelpPageManager {
    private Stage helpStage;
    private String page;

    public HelpPageManager(){
        page = "";
    }

    public void openHelpPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            helpStage = new Stage();

            helpStage.setOnCloseRequest(event -> {
                help.setDisable(false);
            });

            helpStage.setScene(scene);
            helpStage.setResizable(false);
            helpStage.initStyle(StageStyle.UTILITY);

            helpStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeHelpPage(){
        helpStage.close();
    }

    public void setHelpPage(String p){
        page = p;
    }

    public Stage getHelpStage() {
        return helpStage;
    }
}
