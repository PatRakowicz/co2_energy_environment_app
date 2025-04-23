package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.UpdateUtilityLogic;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


public class UpdateDataController{
    private DBConn dbConn;

    @FXML
    private Tab utilityTab, gasTab, buildingTab;

    private UpdateUtilityController updateUtilityController;
    private UpdateGasController updateGasController;
    private UpdateBuildingController updateBuildingController;
    private GridPane utilityPane, gasPane, buildingPane;


    public UpdateDataController(){}

    public UpdateDataController(DBConn conn){
        // set initial connection
        dbConn = conn;

        // set tab controllers
        updateUtilityController = new UpdateUtilityController(dbConn);
        updateGasController = new UpdateGasController(dbConn);
        updateBuildingController = new UpdateBuildingController(dbConn);

        // get tab content
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/update-utility-tab.fxml"));
            fxmlLoader.setController(updateUtilityController);
            utilityPane = fxmlLoader.load();

            fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/update-gas-tab.fxml"));
            fxmlLoader.setController(updateGasController);
            gasPane = fxmlLoader.load();

            fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/update-building-tab.fxml"));
            fxmlLoader.setController(updateBuildingController);
            buildingPane = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        //set tab content
        utilityTab.setContent(utilityPane);
        gasTab.setContent(gasPane);
        buildingTab.setContent(buildingPane);
    }
}
