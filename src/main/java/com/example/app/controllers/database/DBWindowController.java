package com.example.app.controllers.database;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DBWindowController {
    @FXML
    private TextField ipField;
    @FXML
    private PasswordField passwordField;


    public void handleSubmit(ActionEvent event){
        // this is where the logic of submitting the information goes



        //if the info submits properly this code closes the popup window
        Stage stage = (Stage) ipField.getScene().getWindow();
        stage.close();
    }
}
