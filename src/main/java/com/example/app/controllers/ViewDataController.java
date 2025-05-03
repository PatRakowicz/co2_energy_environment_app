package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.GasRecords;
import com.example.app.dao.UtilityRecords;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import com.example.app.model.Gas;
import com.example.app.model.Utility;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewDataController{
    private DBConn dbConn;
    private BuildingRecords buildingRecords;
    private ArrayList<Building> buildings;
    private UtilityRecords utilityRecords;
    private GasRecords gasRecords;
    private FilteredBuildingBox filteredBuildingBox;

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
    // Table View
    @FXML private SplitPane mainSplitPane;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private TabPane tabPane;
    @FXML private Tab utilityTab;
    @FXML private Tab gasTab;
    @FXML private TableView<Utility> utilityTableView;
    @FXML private TableView<Gas> gasTableView;

    @FXML private TableColumn<Utility, String> dateColumn;
    @FXML private TableColumn<Utility, Float> electricityUsageColumn;
    @FXML private TableColumn<Utility, Float> waterUsageColumn;
    @FXML private TableColumn<Utility, Float> electricityCostColumn;
    @FXML private TableColumn<Utility, Float> waterCostColumn;
    @FXML private TableColumn<Utility, Float> sewageCostColumn;
    @FXML private TableColumn<Utility, Float> miscCostColumn;

    @FXML private TableColumn<Gas, String> rateColumn;
    @FXML private TableColumn<Gas, Float> currentChargeColumn;
    @FXML private TableColumn<Gas, String> fromBillingColumn;
    @FXML private TableColumn<Gas, String> toBillingColumn;
    @FXML private TableColumn<Gas, Float> meterReadColumn;
    @FXML private TableColumn<Gas, Float> billedCCFColumn;


    public ViewDataController(){}

    public ViewDataController(DBConn conn){
        dbConn = conn;
    }

    public void initialize() {
        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        filteredBuildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        utilityRecords = new UtilityRecords(dbConn);
        gasRecords = new GasRecords(dbConn);

        // Enable manual typing in datePicker
        startDatePicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            try {
                if (!isNowFocused) {
                    startDatePicker.setValue(startDatePicker.getConverter()
                            .fromString(startDatePicker.getEditor().getText()));
                }
            } catch (Exception e) {
                startDatePicker.setValue(null);
            }
        });
        endDatePicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            try {
                if (!isNowFocused) {
                    endDatePicker.setValue(endDatePicker.getConverter()
                            .fromString(endDatePicker.getEditor().getText()));
                }
            } catch (Exception e) {
                endDatePicker.setValue(null);
            }
        });


        // https://docs.oracle.com/javase/8/javafx/api/javafx/beans/property/SimpleFloatProperty.html#SimpleFloatProperty-java.lang.Object-java.lang.String-
        // Properties can be bound in ways that will automatically update the UI when something changes
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        electricityUsageColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getElectricityUsage()).asObject());
        waterUsageColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getWaterUsage()).asObject());
        electricityCostColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getElectricityCost()).asObject());
        waterCostColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getWaterCost()).asObject());
        sewageCostColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getSewageCost()).asObject());
        miscCostColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getMiscCost()).asObject());

        fromBillingColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFromBilling().toString()));
        toBillingColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getToBilling().toString()));
        rateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRate()));
        currentChargeColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getCurrentCharges()).asObject());
        meterReadColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getMeterRead()).asObject());
        billedCCFColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getBilledCCF()).asObject());


    }

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

        ArrayList<Utility> utilities = utilityRecords.getUtilities(building.getBuildingID(), startDate, endDate, dbConn);

        populateCharts(utilities, showElectricityUsage, showWaterUsage, showElectricityCost,
                        showWaterCost, showSewageCost, showMiscCost);

        ArrayList<Gas> gasList = gasRecords.getGas(building.getBuildingID(), startDate, endDate, dbConn);

        populateTables(utilities, gasList);
    }

    private void populateTables(ArrayList<Utility> utilities, ArrayList<Gas> gasList) {
        // Utility
        utilityTableView.getItems().clear();

        ObservableList<Utility> oUtilities = FXCollections.observableArrayList(utilities);
        utilityTableView.setItems(oUtilities);

        // Gas
        gasTableView.getItems().clear();

        ObservableList<Gas> oGas = FXCollections.observableArrayList(gasList);
        gasTableView.setItems(oGas);
    }

    private void populateCharts(ArrayList<Utility> utilities, boolean showElectricityUsage, boolean showWaterUsage, boolean showElectricityCost,
                                boolean showWaterCost, boolean showSewageCost, boolean showMiscCost) {
        // LineChart
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

            Float eu = utility.getElectricityUsage();   // Electricity Usage
            if (electricityUsage != null && eu != null) {
                electricityUsage.getData().add(new XYChart.Data<>(dateToString, eu));
            }

            Float wu = utility.getWaterUsage();     // Water Usage
            if (waterUsage != null && wu != null) {
                waterUsage.getData().add(new XYChart.Data<>(dateToString, wu));
            }

            Float ec = utility.getElectricityCost();    // Electricity Cost
            if (electricityCost != null && ec != null) {
                electricityCost.getData().add(new XYChart.Data<>(dateToString, ec));
            }

            Float wc = utility.getWaterCost();      // Water Cost
            if (waterCost != null && wc != null) {
                waterCost.getData().add(new XYChart.Data<>(dateToString, wc));
            }

            Float sc = utility.getSewageCost();     // Sewage Cost
            if (sewageCost != null && sc != null) {
                sewageCost.getData().add(new XYChart.Data<>(dateToString, sc));
            }

            Float mc = utility.getMiscCost();       // Misc Cost
            if (miscCost != null && mc != null) {
                miscCost.getData().add(new XYChart.Data<>(dateToString, mc));
            }
        }

        /*
        Was trying to ignore the NULL values in the database but when the following if statements are executed
        with the types in the Utility model set to Float so they can capture NULL, nothing is populated in the
        bar chart for some reason.
         */

//        if (electricityUsage != null) {
//            Float value = utility.getElectricityUsage();
//            if (value != null) {
//                lineChart.getData().add(electricityUsage);
//            }
//        }
// This is currently only checking if the last utility in the list. Previous utilities with values are skipped leading
// to the graph being empty in the majority of situations.
        if (electricityUsage != null && !electricityUsage.getData().isEmpty()) {
            lineChart.getData().add(electricityUsage);
        }
        if (waterUsage != null && !waterUsage.getData().isEmpty()) {
            lineChart.getData().add(waterUsage);
        }
        if (electricityCost != null && !electricityCost.getData().isEmpty()) {
            lineChart.getData().add(electricityCost);
        }
        if (waterCost != null && !waterCost.getData().isEmpty()) {
            lineChart.getData().add(waterCost);
        }
        if (sewageCost != null && !sewageCost.getData().isEmpty()) {
            lineChart.getData().add(sewageCost);
        }
        if (miscCost != null && !miscCost.getData().isEmpty()) {
            lineChart.getData().add(miscCost);
        }

        // Bar Chart
        barChart.getData().clear();

        Map<String, Float[]> usageDictionary = utilityRecords.getBuildingTotalUsage();

        XYChart.Series<String, Number> electricSeries = new XYChart.Series<>();
        electricSeries.setName("Total KWH (x1000)");

        XYChart.Series<String, Number> waterSeries = new XYChart.Series<>();
        waterSeries.setName("Total GAL (x1000)");

        usageDictionary.entrySet().stream()
                .sorted((a, b) -> {
                    Float[] aVals = a.getValue();
                    Float[] bVals = b.getValue();

                    Float electricity = aVals[0];
                    Float water = aVals[1];
                    Float electricityUsageA = (electricity != null) ? electricity : 0;    // Electricity Usage
                    Float waterUsageA = (water != null) ? water : 0;                 // Water Usage
                    Float aTotal = electricityUsageA + waterUsageA;

                    Float electricityB = bVals[0];
                    Float waterB = bVals[1];
                    Float electricityUsageB = (electricityB != null) ? electricityB : 0;
                    Float waterUsageB = (waterB != null) ? waterB : 0;
                    Float bTotal = electricityUsageB + waterUsageB;

                    return Float.compare(bTotal, aTotal); // descending bars
                })
                .limit(20)
                .forEach(entry -> {
                    String building = entry.getKey();
                    Float kwh = entry.getValue()[0] != null ? entry.getValue()[0] : 0;
                    Float gallons = entry.getValue()[1] != null ? entry.getValue()[1] : 0;

                    electricSeries.getData().add(new XYChart.Data<>(building, kwh));
                    waterSeries.getData().add(new XYChart.Data<>(building, gallons));
                });

        barChart.getData().addAll(electricSeries, waterSeries);
        barChart.setLegendVisible(true);
        barChart.getXAxis().setTickLabelRotation(-45);
    }
}
