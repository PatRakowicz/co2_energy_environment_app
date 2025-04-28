package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.MasterMeterLogic;
import com.example.app.model.Building;
import com.example.app.utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Date;
import java.util.ArrayList;

public class AddBuildingController implements Alerts {
    private DBConn dbConn;
    ArrayList<Building> buildings;
    @FXML
    private Label nameError, locationError, squareFeetError, constructionDateError, startSharedError, endSharedError;
    @FXML
    private TextField buildingName, buildingLocation, squareFeet;
    @FXML
    private DatePicker constructionDate, startShared, endShared;
    private String name, loc;
    private int sqft;
    private Date cDate, sShared, eShared;
    private BuildingRecords buildingRecords;

    public AddBuildingController(){}

    public AddBuildingController(DBConn c){
        dbConn = c;
    }

    public void initialize() {
        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();

        constructionDate.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                constructionDate.setValue(constructionDate.getConverter()
                        .fromString(constructionDate.getEditor().getText()));
            }
        });

        startShared.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                startShared.setValue(startShared.getConverter()
                        .fromString(startShared.getEditor().getText()));
            }
        });

        endShared.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                endShared.setValue(endShared.getConverter()
                        .fromString(endShared.getEditor().getText()));
            }
        });
    }

    public void clearErrors(){
        nameError.setText(null);
        locationError.setText(null);
        squareFeetError.setText(null);
        constructionDateError.setText(null);
        startSharedError.setText(null);
        endSharedError.setText(null);
    }

    public void clearInputs(){
        buildingName.setText(null);
        buildingLocation.setText(null);
        squareFeet.setText(null);
        constructionDate.setValue(null);
        startShared.setValue(null);
        endShared.setValue(null);
    }

    public void clearStored(){
        loc = null;
        sqft = 0;
        cDate = null;
        sShared = null;
        eShared = null;
    }

    public boolean validity(){
        clearStored();

        boolean valid = true;

        if(buildingName.getText() == null || buildingName.getText().isEmpty()){
            nameError.setText("ERROR: building needs a name");
            valid = false;
        }else {
            name = buildingName.getText();
            for (Building b : buildings) {
                if (b.getName().equals(name)) {
                    nameError.setText("ERROR: Can't use the name of an existing building");
                    valid = false;
                }
            }
        }

        if(startShared.getValue() != null){
            sShared = Date.valueOf(startShared.getValue());
            if(squareFeet.getText() == null){
                squareFeetError.setText("ERROR: Square Feet can't be nothing if building id on the master meter");
                valid = false;
            }
        }

        if(startShared.getValue() == null){
            if(endShared.getValue() != null){
                startSharedError.setText("ERROR: Missing date here");
                valid = false;
            }
        }

        if(startShared.getValue() != null && endShared.getValue() != null){
            eShared = Date.valueOf(endShared.getValue());
            if(eShared.before(sShared)){
                endSharedError.setText("ERROR: building can't be removed from master meter before it is added");
                valid = false;
            } else if (eShared.equals(sShared)) {
                startSharedError.setText("ERROR: Building can't be added to master meter and removed from it on the same day");
                valid = false;
            }
        }

        if(constructionDate.getValue() == null){
            constructionDateError.setText("ERROR: Construction Date can't be empty");
            valid = false;
        }else{
            cDate = Date.valueOf(constructionDate.getValue());
            if(sShared != null && sShared.before(cDate)){
                startSharedError.setText("ERROR: Building can't be on master meter before Construction Date");
                valid = false;
            }
        }

        if(squareFeet.getText() == null || squareFeet.getText().isEmpty()){
            sqft = -1;
        }else {
            if (squareFeet.getText() != null) {
                try {
                    sqft = Integer.parseInt(squareFeet.getText());
                    if(sqft < 0){
                        squareFeetError.setText("ERROR: Square Feet can't be negative");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    squareFeetError.setText("ERROR: Square Feet must be a number");
                    valid = false;
                }
            }
        }

        loc = buildingLocation.getText();

        return valid;
    }

    public void add(){
        clearErrors();
        if(validity()){
            try {
                //load resource file into new stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/building-add-confirmation.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.initStyle(StageStyle.UTILITY);

                Button yes = (Button) scene.lookup("#yes");
                Button no = (Button) scene.lookup("#no");

                yes.setOnAction(e -> {
                    Building building = new Building();

                    building.setName(name);
                    building.setLocation(loc);
                    building.setSqFT(sqft);
                    building.setDate(cDate);
                    building.setStartShared(sShared);
                    building.setEndShared(eShared);

                    BuildingRecords buildingRecords = new BuildingRecords(dbConn);
                    boolean success = buildingRecords.insertBuilding(building);

                    if (success) {
                        // Log inserted data here
                        stage.close();
                        if(sShared != null){
                            MasterMeterLogic masterMeterLogic = new MasterMeterLogic(dbConn);
                            masterMeterLogic.updateAllFrom(sShared);
                        }else{
                            insertSuccessful();
                        }
                        clearInputs();
                        clearStored();
                    } else {
                        stage.close();
                        insertFail();
                    }
                });

                no.setOnAction(e -> {
                    stage.close();
                });

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
