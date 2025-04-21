package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.model.Building;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.util.ArrayList;

public class AddBuildingController {
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

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    public void initialize() {
        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
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

    public boolean validity(){
        boolean valid = true;

        if(buildingName.getText().isEmpty()){
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
            if(squareFeet.getText().isEmpty()){
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

        if(!squareFeet.getText().isEmpty()){
            try{
                sqft = Integer.parseInt(squareFeet.getText());
            } catch (NumberFormatException e) {
                squareFeetError.setText("ERROR: Square Feet must be a number");
                valid = false;
            }
        }

        loc = buildingLocation.getText();

        return valid;
    }

    public void add(){
        clearErrors();
        if(validity()){
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
                alertSuccsess();
                clearInputs();
            } else {
                alertFailure();
            }
        }
    }

    public void alertSuccsess(){
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setContentText("Data Inserted Successfully!");
        a.show();
    }

    public void alertFailure(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText("Failed to insert data \n Please try again or check database connection");
        a.show();
    }
}
