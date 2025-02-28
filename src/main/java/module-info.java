module com.example.MainApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.MainApp to javafx.fxml;
    exports com.example.MainApp;
}