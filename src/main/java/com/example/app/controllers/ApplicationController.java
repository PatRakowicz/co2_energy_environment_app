package com.example.app.controllers;

import com.example.app.dao.DBConn;
import com.example.app.utils.HelpPageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class ApplicationController{
    @FXML
    private VBox rootPane;

    private Stage DBStage;

    @FXML
    private Button DBButton;
    @FXML
    private ImageView helpButton;
    public static ImageView help;
    public static HelpPageManager helpPageManager;
    private DBConn dbConn;
    private ViewDataController viewDataController;

    @FXML
    public void initialize() {
        try {
            dbConn = new DBConn();
            updateDBButtonStatus();
            viewDataController = new ViewDataController(dbConn);


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/view-data-view.fxml"));
            fxmlLoader.setController(viewDataController);
            BorderPane content = fxmlLoader.load();
            rootPane.getChildren().add(content);
            VBox.setVgrow(content, Priority.ALWAYS);
            content.prefWidthProperty().bind(rootPane.widthProperty());
            content.prefHeightProperty().bind(rootPane.heightProperty().subtract(40));

            setHelp(helpButton);
            helpPageManager = new HelpPageManager();
            helpPageManager.setHelpPage("/fxml/help-view.fxml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetPages(){
        updateDBButtonStatus();
        viewDataController = new ViewDataController(dbConn);
        setPageContent("/fxml/view-data-view.fxml", viewDataController);
    }

    public void setPageContent(String fxmlFile, Object controller){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            fxmlLoader.setController(controller);
            BorderPane content = fxmlLoader.load();

            rootPane.getChildren().set(1,content);

            VBox.setVgrow(content, Priority.ALWAYS);
            content.prefWidthProperty().bind(rootPane.widthProperty());
            content.prefHeightProperty().bind(rootPane.heightProperty().subtract(40));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateDBButtonStatus() {
        if (dbConn.isConnectionSuccessful()) {
            if (DBButton != null) {
                DBButton.setStyle("-fx-background-color: LimeGreen");
            }
        } else {
            if (DBButton != null) {
                DBButton.setStyle("-fx-background-color: #f74545");
            }
        }
    }

    public void openDBWindow() {
        boolean isConnected = dbConn.isConnectionSuccessful();
        try {
            if (DBStage != null && DBStage.isShowing()) {
                return;
            }

            DBButton.setDisable(true);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBWindow.fxml"));
            loader.setController(dbConn);
            Parent DBRoot = loader.load();
            
            Scene DBScene = new Scene(DBRoot);
            DBStage = new Stage();
            DBStage.initModality(Modality.APPLICATION_MODAL);
            DBStage.setScene(DBScene);
            DBStage.setResizable(false);
            DBStage.initStyle(StageStyle.UTILITY);

            DBStage.setOnHidden(windowEvent -> {
                DBButton.setDisable(false);
                updateDBButtonStatus();
                if(dbConn.isConnectionSuccessful() && !isConnected){
                    resetPages();
                }
            });

            DBStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openHelp(){
        if(helpButton.isDisable()){
            return;
        }
        helpButton.setDisable(true);
        helpPageManager.openHelpPage();
    }

    public static void setHelp(ImageView imageView){
        help = imageView;
    }
}
