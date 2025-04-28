package com.example.app.controllers;

import com.example.app.dao.DBConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class ApplicationController {
    @FXML
    private VBox rootPane;

    private Stage DBStage;

    @FXML
    private Button DBButton, addPageButton, updatePageButton, viewPageButton, reportPageButton;

    private DBConn dbConn;

    private AddDataController addDataController;
    private LogController logController;
    private UpdateDataController updateDataController;
    private ViewDataController viewDataController;

    @FXML
    public void initialize() {
        try {
            dbConn = new DBConn();
            updateDBButtonStatus();
            addDataController = new AddDataController(dbConn);
            logController = new LogController(dbConn);
            updateDataController = new UpdateDataController(dbConn);
            viewDataController = new ViewDataController(dbConn);


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/view-data-view.fxml"));
            fxmlLoader.setController(viewDataController);
            BorderPane content = fxmlLoader.load();
            rootPane.getChildren().add(content);
            VBox.setVgrow(content, Priority.ALWAYS);
            content.prefWidthProperty().bind(rootPane.widthProperty());
            content.prefHeightProperty().bind(rootPane.heightProperty().subtract(40));
            viewPageButton.setDisable(true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetPages(){
        updateDBButtonStatus();
        addDataController = new AddDataController(dbConn);
        reportController = new ReportController();
        updateDataController = new UpdateDataController(dbConn);
        viewDataController = new ViewDataController(dbConn);
        switchToViewData(new ActionEvent());
        viewPageButton.setDisable(true);
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

    public void enableButtons(){
        addPageButton.setDisable(false);
        reportPageButton.setDisable(false);
        viewPageButton.setDisable(false);
        updatePageButton.setDisable(false);
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
    public void switchToAddData(ActionEvent event) {
        enableButtons();
        addPageButton.setDisable(true);
        setPageContent("/fxml/add-data-view.fxml", addDataController);
    }

    @FXML
    public void switchToReport(ActionEvent event) {
        enableButtons();
        reportPageButton.setDisable(true);
        setPageContent("/fxml/log-view.fxml", logController);
        logController.initialize();
    }

    @FXML
    public void switchToUpdateData(ActionEvent event) {
        enableButtons();
        updatePageButton.setDisable(true);
        setPageContent("/fxml/update-data-view.fxml", updateDataController);
    }

    @FXML
    public void switchToViewData(ActionEvent event) {
        enableButtons();
        viewPageButton.setDisable(true);
        setPageContent("/fxml/view-data-view.fxml", viewDataController);
    }
}
