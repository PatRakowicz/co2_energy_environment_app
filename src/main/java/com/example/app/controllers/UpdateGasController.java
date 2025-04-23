package com.example.app.controllers;

import com.example.app.dao.DBConn;
import com.example.app.model.Building;

import java.util.ArrayList;

public class UpdateGasController {
    private DBConn dbConn;
    private ArrayList<Building> buildings;


    public UpdateGasController(){}

    public UpdateGasController(DBConn c){
        dbConn = c;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }
}
