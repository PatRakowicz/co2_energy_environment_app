package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.model.Building;
import com.example.app.model.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.time.LocalDate;

public class ViewDataController extends ApplicationController{
    private BuildingRecords buildingRecords;

    // Left Side Panel
    @FXML private ComboBox<Building> buildingComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private CheckBox electricityUsageCheck;
    @FXML private CheckBox waterUsageCheck;
    @FXML private CheckBox electricityCostCheck;
    @FXML private CheckBox waterCostCheck;
    @FXML private CheckBox sewageCostCheck;
    @FXML private CheckBox miscCostCheck;

    // Center Panel
//    @FXML private LineChart<String, Number> lineChart;
//    @FXML private TableView<Data> dataTable;

    @FXML public void loadSelections(ActionEvent event) {
        Building building = buildingComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        boolean showElectricityUsage = electricityUsageCheck.isSelected();
        boolean showWaterUsage = waterUsageCheck.isSelected();
        boolean showElectricityCost = electricityCostCheck.isSelected();
        boolean showWaterCost = waterCostCheck.isSelected();
        boolean showSewageCost = sewageCostCheck.isSelected();
        boolean showMiscCost = miscCostCheck.isSelected();
    }

    @Override
    public void initialize() {
        super.initialize();

        if (dbController == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(super.dbController);
        buildings = buildingRecords.getBuildings();

        ObservableList<Building> oBuildings = FXCollections.observableArrayList(buildings);
        buildingComboBox.setItems(oBuildings);

        buildingComboBox.setConverter(new StringConverter<Building>() {
            @Override
            public String toString(Building building) {
                if (building == null) {
                    return "";
                }
                return building.getName();
            }

            @Override
            public Building fromString(String s) {
                return null;
            }
        });
    }
}
