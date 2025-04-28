package com.example.app.controllers;
import com.example.app.dao.DBConn;

public class LogController {
    private DBConn dbConn;

    public LogController() {}

    public LogController(DBConn dbConn) {
        this.dbConn = dbConn;


    }


}
