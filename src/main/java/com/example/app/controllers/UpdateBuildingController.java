package com.example.app.controllers;

import com.example.app.dao.DBConn;
import com.example.app.model.Building;

import java.util.ArrayList;

public class UpdateBuildingController {
    private DBConn dbConn;
    private ArrayList<Building> buildings;


    public UpdateBuildingController(){}

    public UpdateBuildingController(DBConn c){
        dbConn = c;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }
}
