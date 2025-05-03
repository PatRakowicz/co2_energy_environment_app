package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.UpdateUtilityLogic;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import com.example.app.utils.HelpPageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

import static com.example.app.controllers.ApplicationController.getHelp;
import static com.example.app.controllers.ApplicationController.helpPageManager;


public class UpdateDataController{
    private DBConn dbConn;

    @FXML
    private Tab utilityTab, gasTab, buildingTab;
    @FXML
    private TabPane updateDataPane;

    private UpdateUtilityController updateUtilityController;
    private UpdateGasController updateGasController;
    private UpdateBuildingController updateBuildingController;
    private GridPane utilityPane, gasPane, buildingPane;


    public UpdateDataController(){}

    public UpdateDataController(DBConn conn){
        // set initial connection
        dbConn = conn;

        // if the connection isn't there exit
        if(dbConn == null){
            return;
        }

        // set tab controllers
        updateUtilityController = new UpdateUtilityController(dbConn);
        updateGasController = new UpdateGasController(dbConn);
        updateBuildingController = new UpdateBuildingController(dbConn);

        // get tab content
        try{
            if(dbConn != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/update-utility-tab.fxml"));
                fxmlLoader.setController(updateUtilityController);
                utilityPane = fxmlLoader.load();

                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/update-gas-tab.fxml"));
                fxmlLoader.setController(updateGasController);
                gasPane = fxmlLoader.load();

                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/update-building-tab.fxml"));
                fxmlLoader.setController(updateBuildingController);
                buildingPane = fxmlLoader.load();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        // set help page content
        updateDataPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                updateHelpPage(newTab);
            }
        });

        //set tab content
        utilityTab.setContent(utilityPane);
        gasTab.setContent(gasPane);
        buildingTab.setContent(buildingPane);
        updateHelpPage(updateDataPane.getSelectionModel().getSelectedItem());
    }

    public void updateHelpPage(Tab newTab){
        if(helpPageManager.getHelpStage() != null) {
            helpPageManager.closeHelpPage();
        }
        if(newTab == utilityTab){
            helpPageManager.setHelpPage("/fxml/help-update-utility.fxml");
        }
        else if(newTab == gasTab){
            helpPageManager.setHelpPage("/fxml/help-update-gas.fxml");
        }
        else if(newTab == buildingTab){
            helpPageManager.setHelpPage("/fxml/help-update-building.fxml");
        }
        getHelp().setDisable(false);
    }
}
