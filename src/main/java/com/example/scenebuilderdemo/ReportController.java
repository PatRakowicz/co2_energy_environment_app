package com.example.scenebuilderdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReportController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToHome(ActionEvent event){
        try {
            root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("home");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
