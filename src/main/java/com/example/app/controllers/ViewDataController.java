package com.example.app.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewDataController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToHome(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/home-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.setMinHeight(600);
            stage.setMinWidth(400);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
