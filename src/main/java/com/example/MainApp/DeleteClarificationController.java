package com.example.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DeleteClarificationController extends UpdateDataController{
    @FXML
    private Label question;

    public void yes(){
        //delete entry in database

        Stage stage = (Stage) question.getScene().getWindow();
        stage.close();

    }

    public void no(){
        Stage stage = (Stage) question.getScene().getWindow();
        stage.close();
    }

}
