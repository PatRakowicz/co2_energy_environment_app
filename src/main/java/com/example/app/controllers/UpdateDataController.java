package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.UpdateViewLogic;
import com.example.app.model.Building;
import com.example.app.model.FilteredBuildingBox;
import com.example.app.model.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;


public class UpdateDataController extends ApplicationController {
    private BuildingRecords buildingRecords;
    private UpdateViewLogic updateViewLogic;
    private FilteredBuildingBox buildingBox;

    @FXML private Label electricityUsageError, electricityCostError, waterUsageError,
            waterCostError, sewageCostError, miscCostError, dateError, buildingError;

    @FXML private TextField electricityUsage, electricityCost, waterUsage, waterCost, sewageCost, miscCost;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Building> buildingComboBox;
    @FXML private Button updateButton, deleteButton;

    private float eUsage, eCost, wUsage, wCost, sCost, mCost;
    private LocalDate date;

    @Override
    public void initialize() {
        super.initialize();

        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);
        updateViewLogic = new UpdateViewLogic(dbConn);
    }

    public void onChange() {
        Building selectedBuilding = buildingComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedBuilding != null) {
            buildingBox.lastNotNull = selectedBuilding;
        }

        if (selectedDate != null && selectedBuilding != null) {
            Utility utility = updateViewLogic.getUtilityForDate(selectedBuilding.getBuildingID(), selectedDate);

            if (utility != null) {
                electricityUsage.setText(String.valueOf(utility.getElectricityUsage()));
                electricityCost.setText(String.valueOf(utility.getElectricityCost()));
                waterUsage.setText(String.valueOf(utility.getWaterUsage()));
                waterCost.setText(String.valueOf(utility.getWaterCost()));
                sewageCost.setText(String.valueOf(utility.getSewageCost()));
                miscCost.setText(String.valueOf(utility.getMiscCost()));
            } else {
                clearInputs();
            }

            setInputDisabled(false);
        } else {
            setInputDisabled(true);
        }
    }

    public void update() {
        clearErrors();

        if (validity()) {
            Utility utility = new Utility();
            utility.setDate(java.sql.Date.valueOf(date));
            utility.setElectricityUsage(eUsage);
            utility.setElectricityCost(eCost);
            utility.setWaterUsage(wUsage);
            utility.setWaterCost(wCost);
            utility.setSewageCost(sCost);
            utility.setMiscCost(mCost);

            Building building = buildingComboBox.getValue();
            boolean success = updateViewLogic.insertUtility(building, utility);

            if (success) clearInputs();
            else System.out.println("Failed to insert utility record.");
        }
    }

    public void delete() {
        // TODO: implement delete logic
    }

    private boolean validity() {
        boolean valid = true;

        try {
            eUsage = Float.parseFloat(electricityUsage.getText());
        } catch (NumberFormatException e) {
            if (!electricityUsage.getText().isEmpty()) {
                electricityUsageError.setText("ERROR: Electricity Usage must be a number");
                valid = false;
            }
        }

        try {
            eCost = Float.parseFloat(electricityCost.getText());
        } catch (NumberFormatException e) {
            if (!electricityCost.getText().isEmpty()) {
                electricityCostError.setText("ERROR: Electricity Cost must be a number");
                valid = false;
            }
        }

        try {
            wUsage = Float.parseFloat(waterUsage.getText());
        } catch (NumberFormatException e) {
            if (!waterUsage.getText().isEmpty()) {
                waterUsageError.setText("ERROR: Water Usage must be a number");
                valid = false;
            }
        }

        try {
            wCost = Float.parseFloat(waterCost.getText());
        } catch (NumberFormatException e) {
            if (!waterCost.getText().isEmpty()) {
                waterCostError.setText("ERROR: Water Cost must be a number");
                valid = false;
            }
        }

        try {
            sCost = Float.parseFloat(sewageCost.getText());
        } catch (NumberFormatException e) {
            if (!sewageCost.getText().isEmpty()) {
                sewageCostError.setText("ERROR: Sewage Cost must be a number");
                valid = false;
            }
        }

        try {
            mCost = Float.parseFloat(miscCost.getText());
        } catch (NumberFormatException e) {
            if (!miscCost.getText().isEmpty()) {
                miscCostError.setText("ERROR: Misc. Cost must be a number");
                valid = false;
            }
        }

        if (datePicker.getValue() == null) {
            dateError.setText("ERROR: Invalid date");
            valid = false;
        } else {
            date = datePicker.getValue();
        }

        if (buildingComboBox.getValue() == null) {
            buildingError.setText("ERROR: Building must be selected");
            valid = false;
        }

        return valid;
    }

    private void clearInputs() {
        electricityUsage.setText("");
        electricityCost.setText("");
        waterUsage.setText("");
        waterCost.setText("");
        sewageCost.setText("");
        miscCost.setText("");
        datePicker.setValue(null);
    }

    private void clearErrors() {
        electricityUsageError.setText("");
        electricityCostError.setText("");
        waterUsageError.setText("");
        waterCostError.setText("");
        sewageCostError.setText("");
        miscCostError.setText("");
        dateError.setText("");
        buildingError.setText("");
    }

    private void setInputDisabled(boolean disabled) {
        electricityUsage.setDisable(disabled);
        electricityCost.setDisable(disabled);
        waterUsage.setDisable(disabled);
        waterCost.setDisable(disabled);
        sewageCost.setDisable(disabled);
        miscCost.setDisable(disabled);
        updateButton.setDisable(disabled);
        deleteButton.setDisable(disabled);
    }
}
