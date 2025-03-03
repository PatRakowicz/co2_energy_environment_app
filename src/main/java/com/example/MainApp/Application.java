package com.example.MainApp;

import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
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