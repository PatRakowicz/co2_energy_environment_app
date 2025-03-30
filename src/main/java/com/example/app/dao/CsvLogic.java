package com.example.app.dao;

import com.example.app.controllers.DBController;

import java.io.File;

public class CsvLogic implements DBQueries{
    private final DBController dbController;

    private final String[] HEADERS = {
            "Building Name", "Water Usage", "Water Cost", "Electricity Usage", "Electricity Cost",
            "Sewage Cost", "Misc Usage", "Misc Cost", "Date (YYYY-MM-DD)"
    };

    public CsvLogic(DBController dbController) {
        this.dbController = dbController;
    }


    public void importUtilityCSV(File file) {
    }

    public void exportCsvTemplate(File file) {
    }
}
