package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.UpdateViewLogic;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import com.example.app.model.Utility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;


public class UpdateDataController{
    private DBConn dbConn;
    private BuildingRecords buildingRecords;
    private ArrayList<Building> buildings;
    private UpdateViewLogic updateViewLogic;
    private FilteredBuildingBox buildingBox;


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

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();

        //update building list in each tab controller
        updateUtilityController.setBuildings(buildings);
        updateGasController.setBuildings(buildings);
        updateBuildingController.setBuildings(buildings);

        //set tab content
        utilityTab.setContent(utilityPane);
        gasTab.setContent(gasPane);
        buildingTab.setContent(buildingPane);
    }
}
