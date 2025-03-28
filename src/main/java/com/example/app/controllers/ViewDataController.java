package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.UtilityRecords;
import com.example.app.model.Building;
import com.example.app.model.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;

public class ViewDataController extends ApplicationController{
    private BuildingRecords buildingRecords;
    private UtilityRecords utilityRecords;

    private Utility utility = new Utility();

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
    @FXML private LineChart<String, Number> lineChart;
    @FXML private TableView<Utility> tableView;

    @FXML public void refreshView(ActionEvent event) {
        Building building = buildingComboBox.getValue();
        if (building == null) {
            // We need to create a view that displays errors, similar to the database view
            System.out.println("Building Not Selected.");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null) {
            // send error to user
            System.out.println("Start Date not selected.");
            return;
        } else if (endDate == null) {
            // send error to user
            System.out.println("End date not selected.");
            return;
        }

        boolean showElectricityUsage = electricityUsageCheck.isSelected();
        boolean showWaterUsage = waterUsageCheck.isSelected();
        boolean showElectricityCost = electricityCostCheck.isSelected();
        boolean showWaterCost = waterCostCheck.isSelected();
        boolean showSewageCost = sewageCostCheck.isSelected();
        boolean showMiscCost = miscCostCheck.isSelected();

        ArrayList<Utility> utilities = utilityRecords.getUtilities(building.getBuildingID(), startDate, endDate);

        populateChart(utilities, showElectricityUsage, showWaterUsage, showElectricityCost,
                        showWaterCost, showSewageCost, showMiscCost);

        populateTable(utilities);
    }

    private void populateTable(ArrayList<Utility> utilities) {
        tableView.getItems().clear();

        ObservableList<Utility> oUtilities = FXCollections.observableArrayList(utilities);
        tableView.setItems(oUtilities);
    }

    private void populateChart(ArrayList<Utility> utilities, boolean showElectricityUsage, boolean showWaterUsage, boolean showElectricityCost,
                               boolean showWaterCost, boolean showSewageCost, boolean showMiscCost) {
        lineChart.getData().clear();
        XYChart.Series<String, Number> electricityUsage = null;
        XYChart.Series<String, Number> waterUsage = null;
        XYChart.Series<String, Number> electricityCost = null;
        XYChart.Series<String, Number> waterCost = null;
        XYChart.Series<String, Number> sewageCost = null;
        XYChart.Series<String, Number> miscCost = null;

        if (electricityUsageCheck.isSelected()) {
            electricityUsage = new XYChart.Series<>();
            electricityUsage.setName("Electricity Usage");
        }

        if (waterUsageCheck.isSelected()) {
            waterUsage = new XYChart.Series<>();
            waterUsage.setName("Water Usage");
        }

        if (electricityCostCheck.isSelected()) {
            electricityCost = new XYChart.Series<>();
            electricityCost.setName("Electricity Cost");
        }

        if (waterCostCheck.isSelected()) {
            waterCost = new XYChart.Series<>();
            waterCost.setName("Water Cost");
        }

        if (sewageCostCheck.isSelected()) {
            sewageCost = new XYChart.Series<>();
            sewageCost.setName("Sewage Cost");
        }

        if (miscCostCheck.isSelected()) {
            miscCost = new XYChart.Series<>();
            miscCost.setName("Misc Cost");
        }

        for (Utility utility : utilities) {
            String dateToString = utility.getDate().toString();
            if (electricityUsage != null) {
                electricityUsage.getData().add(new XYChart.Data<>(dateToString, utility.getElectricityUsage()));
            }
            if (waterUsage != null) {
                waterUsage.getData().add(new XYChart.Data<>(dateToString, utility.getWaterUsage()));
            }
            if (electricityCost != null) {
                electricityCost.getData().add(new XYChart.Data<>(dateToString, utility.getElectricityCost()));
            }
            if (waterCost != null) {
                waterCost.getData().add(new XYChart.Data<>(dateToString, utility.getWaterCost()));
            }
            if (sewageCost != null) {
                sewageCost.getData().add(new XYChart.Data<>(dateToString, utility.getSewageCost()));
            }
            if (miscCost != null) {
                miscCost.getData().add(new XYChart.Data<>(dateToString, utility.getMiscCost()));
            }
        }

        /*
        Was trying to ignore the NULL values in the database but when the following if statements are executed
        with the types in the Utility model set to Float so they can capture NULL, nothing is populated in the
        bar chart for some reason.
         */

        if (electricityUsage != null) {
            Float value = utility.getElectricityUsage();
            if (value != null) {
                lineChart.getData().add(electricityUsage);
            }
        }
        if (waterUsage != null) {
            Float value = utility.getWaterUsage();
            if (value != null) {
                lineChart.getData().add(waterUsage);
            }
        }
        if (electricityCost != null) {
            Float value = utility.getElectricityCost();
            if (value != null) {
                lineChart.getData().add(electricityCost);
            }
        }
        if (waterCost != null) {
            Float value = utility.getWaterCost();
            if (value != null) {
                lineChart.getData().add(waterCost);
            }
        }
        if (sewageCost != null) {
            Float value = utility.getSewageCost();
            if (value != null) {
                lineChart.getData().add(sewageCost);
            }
        }
        if (miscCost != null) {
            Float value = utility.getMiscCost();
            if (value != null) {
                lineChart.getData().add(miscCost);
            }
        }
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

        utilityRecords = new UtilityRecords(super.dbController);
    }
}
