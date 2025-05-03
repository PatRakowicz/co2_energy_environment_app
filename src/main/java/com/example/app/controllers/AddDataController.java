package com.example.app.controllers;

import com.example.app.dao.*;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import com.example.app.model.Utility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class AddDataController {
    private DBConn dbConn;
    @FXML
    private Tab utilityTab, gasTab, buildingTab;

    AddUtilityController addUtilityController;
    AddGasController addGasController;
    AddBuildingController addBuildingController;
    GridPane utilityPane, gasPane, buildingPane;

    public AddDataController(){}

    public AddDataController(DBConn conn){
        //set initial connection
        this.dbConn = conn;

        // if the connection isn't there exit
        if(dbConn == null){
            return;
        }

        //set initial tab controllers
        addUtilityController = new AddUtilityController(dbConn);
        addGasController = new AddGasController(dbConn);
        addBuildingController = new AddBuildingController(dbConn);

        //get tab content
        try {
            if(dbConn != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/add-utility-tab.fxml"));
                fxmlLoader.setController(addUtilityController);
                utilityPane = fxmlLoader.load();

                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/add-gas-tab.fxml"));
                fxmlLoader.setController(addGasController);
                gasPane = fxmlLoader.load();

                fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/add-building-tab.fxml"));
                fxmlLoader.setController(addBuildingController);
                buildingPane = fxmlLoader.load();
            }
        } catch (IOException e) {
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
