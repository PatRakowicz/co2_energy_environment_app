module com.example.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.app to javafx.fxml;
    exports com.example.app;
    exports com.example.app.controllers;
    opens com.example.app.controllers to javafx.fxml;
    exports com.example.app.utils;
    opens com.example.app.utils to javafx.fxml;
    exports com.example.app.dao;
    opens com.example.app.dao to javafx.fxml;
}