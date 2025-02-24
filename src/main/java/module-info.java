module com.example.MainApp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.MainApp to javafx.fxml;
    exports com.example.MainApp;
}