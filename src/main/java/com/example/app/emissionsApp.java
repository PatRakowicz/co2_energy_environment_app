package com.example.app;

import javafx.stage.Stage;
import java.io.IOException;

public class emissionsApp extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            ApplicationController controller = new ApplicationController(stage);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}