package com.example.app.controllers;

import com.example.app.dao.DBConn;
import com.example.app.model.Building;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.ArrayList;

public class ApplicationController {
    @FXML
    private BorderPane rootPane;
    private Scene scene;
    private Stage DBStage;

    private ArrayList<Building> buildings;

    @FXML
    private Button DBButton, addPageButton, updatePageButton, viewPageButton, reportPageButton;;
    private DBConn dbConn;

    private AddDataController addDataController;
    private ReportController reportController;
    private UpdateDataController updateDataController;
    private ViewDataController viewDataController;


    public void setCenterContent(Parent content){
        rootPane.setCenter(content);
    }

    @FXML
    public void initialize() {
        dbConn = new DBConn();
        updateDBButtonStatus();
        addDataController = new AddDataController(dbConn);
        reportController = new ReportController();
        updateDataController = new UpdateDataController(dbConn);
        viewDataController = new ViewDataController(dbConn);

        setCenterContent(loadPage("/fxml/view-data-view.fxml", viewDataController));
        viewPageButton.setDisable(true);
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
        try {
            if (DBStage != null && DBStage.isShowing()) {
                return;
            }

            DBButton.setDisable(true);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBWindow.fxml"));
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
        setCenterContent(loadPage("/fxml/add-data-view.fxml", addDataController));
    }

    @FXML
    public void switchToReport(ActionEvent event) {
        enableButtons();
        reportPageButton.setDisable(true);
        setCenterContent(loadPage("/fxml/report-view.fxml", reportController));
    }

    @FXML
    public void switchToUpdateData(ActionEvent event) {
        enableButtons();
        updatePageButton.setDisable(true);
        setCenterContent(loadPage("/fxml/update-data-view.fxml", updateDataController));
    }

    @FXML
    public void switchToViewData(ActionEvent event) {
        enableButtons();
        viewPageButton.setDisable(true);
        setCenterContent(loadPage("/fxml/view-data-view.fxml", viewDataController));
    }

    private Parent loadPage(String fxmlFile, Object controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            fxmlLoader.setController(controller);
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
