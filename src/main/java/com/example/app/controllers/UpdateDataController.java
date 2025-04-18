package com.example.app.controllers;

import com.example.app.dao.BuildingRecords;
import com.example.app.dao.DBConn;
import com.example.app.dao.UpdateViewLogic;
import com.example.app.model.Building;
import com.example.app.utils.FilteredBuildingBox;
import com.example.app.model.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;


public class UpdateDataController{
    private DBConn dbConn;
    private BuildingRecords buildingRecords;
    private ArrayList<Building> buildings;
    private UpdateViewLogic updateViewLogic;
    private FilteredBuildingBox buildingBox;

    @FXML
    private Label electricityUsageError, electricityCostError, waterUsageError,
            waterCostError, sewageCostError, miscCostError, dateError, buildingError, buildingBoxLabel, monthLabel,
            yearLabel;

    @FXML
    private TextField electricityUsage, electricityCost, waterUsage, waterCost, sewageCost, miscCost;
    @FXML
    private ComboBox<Building> buildingComboBox;
    @FXML
    private Button updateUtilityButton, deleteUtilityButton, loadUtilityDataButton;
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private Tab utilityTab, gasTab, buildingTab;

    private float eUsage, eCost, wUsage, wCost, sCost, mCost;
    private LocalDate date;

    public UpdateDataController(){}

    public UpdateDataController(DBConn conn){
        dbConn = conn;
    }

    public void initialize() {
        if (dbConn == null) {
            System.out.println("No active database connection.");
            return;
        }

        buildingRecords = new BuildingRecords(dbConn);
        buildings = buildingRecords.getBuildings();
        buildingBox = new FilteredBuildingBox(buildings, buildingComboBox);

        updateViewLogic = new UpdateViewLogic(dbConn);

        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        int currentYear = LocalDate.now().getYear();
        int minYear = updateViewLogic.getMinUtilityYear();
        for (int year = currentYear; year >= minYear; year--) {
            yearComboBox.getItems().add(year);
        }
    }


    public void loadUtilityData() {
        Building selectedBuilding = buildingComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();
        int selectedMonthIndex = monthComboBox.getSelectionModel().getSelectedIndex();
        clearInputs();


        if (selectedBuilding == null || selectedYear == null || selectedMonthIndex < 0) {
            System.out.println("Please select both a building and a date before loading data.");
            setDisabledAll(true);
            return;
        }

        LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonthIndex + 1, 1);
        Utility utility = updateViewLogic.getUtilityForDate(selectedBuilding.getBuildingID(), selectedDate);

        if (utility != null) {
            electricityUsage.setText(String.valueOf(utility.getElectricityUsage()));
            electricityCost.setText(String.valueOf(utility.getElectricityCost()));
            waterUsage.setText(String.valueOf(utility.getWaterUsage()));
            waterCost.setText(String.valueOf(utility.getWaterCost()));
            sewageCost.setText(String.valueOf(utility.getSewageCost()));
            miscCost.setText(String.valueOf(utility.getMiscCost()));
            setInputDisabled(false);
        } else {
            System.out.println("No data found for this building and date.");
            clearInputs();
            setInputDisabled(true);
        }
    }

    public void updateUtility() {
        clearErrors();

        if (utilityValidity()) {
            Utility utility = new Utility();
            utility.setDate(java.sql.Date.valueOf(date));
            utility.setElectricityUsage(eUsage);
            utility.setElectricityCost(eCost);
            utility.setWaterUsage(wUsage);
            utility.setWaterCost(wCost);
            utility.setSewageCost(sCost);
            utility.setMiscCost(mCost);

            Building building = buildingComboBox.getValue();
            boolean success = updateViewLogic.updateUtility(building, utility);

            if (success) clearInputs();
            else System.out.println("Failed to insert utility record.");
        }
    }

    public void deleteUtility() {
        // TODO: implement delete logic
    }

    private boolean utilityValidity() {
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

        if (yearComboBox.getValue() == null || monthComboBox.getValue() == null) {
            dateError.setText("ERROR: Month and Year must be selected.");
            valid = false;
        } else {
            int year = yearComboBox.getValue();
            int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            date = LocalDate.of(year, month, 1);
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

    private void setDisabledAll(boolean d){
        electricityUsage.setDisable(d);
        electricityCost.setDisable(d);
        waterUsage.setDisable(d);
        waterCost.setDisable(d);
        sewageCost.setDisable(d);
        miscCost.setDisable(d);
        updateUtilityButton.setDisable(d);
        deleteUtilityButton.setDisable(d);
    }

    private void setDisabledOnMaster(){
        // disable electricity fields
        electricityUsage.setDisable(true);
        electricityCost.setDisable(true);
        // clear electricity fields so no electricity data gets added by mistake
        electricityUsage.setText(null);
        electricityCost.setText(null);
        // enable remaining fields in case they were disabled
        waterCost.setDisable(false);
        waterUsage.setDisable(false);
        sewageCost.setDisable(false);
        miscCost.setDisable(false);
    }

    private void setInputDisabled(boolean disabled) {
        if(!disabled) {
            if (buildingComboBox.getValue().getName().equals("Master Meter")) {
                electricityUsage.setDisable(false);
                electricityCost.setDisable(false);
                miscCost.setDisable(false);
                waterUsage.setDisable(true);
                waterUsage.setText(null);
                waterCost.setDisable(true);
                waterCost.setText(null);
                sewageCost.setDisable(true);
                sewageCost.setText(null);
            } else {
                // get the selected date
                int year = yearComboBox.getValue();
                int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
                LocalDate getDate = LocalDate.of(year, month, 1);
                Date checkDate = Date.from(getDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                Date checkDateStart = buildingComboBox.getValue().getStartShared();
                Date checkDateEnd = buildingComboBox.getValue().getEndShared();

                if(checkDateStart == null && checkDateEnd == null){
                    setDisabledAll(false);
                }
                else if(checkDateStart != null && checkDateEnd == null){
                    setDisabledOnMaster();
                }
                else if(checkDateStart != null && checkDateEnd != null){
                    if(checkDateEnd.before(checkDate) || checkDateEnd == checkDate){
                        setDisabledAll(false);
                    }
                    else{
                        setDisabledOnMaster();
                    }
                }
            }
        }
        else {
            setDisabledAll(true);
        }
    }

    public void tabChanged(){
        if(utilityTab != null && gasTab != null) {
            if (utilityTab.isSelected()) {
                setUtilityDateVisability(true);
                setBuildingBoxVisability(true);
            } else if (gasTab.isSelected()) {
                setUtilityDateVisability(false);
                setBuildingBoxVisability(true);
            }else if (buildingTab.isSelected()){
                setUtilityDateVisability(false);
                setBuildingBoxVisability(true);
            }
        }
    }

    public void setBuildingBoxVisability(boolean b){
        buildingBoxLabel.setVisible(b);
        buildingComboBox.setVisible(b);
        buildingError.setVisible(b);
    }

    public void setUtilityDateVisability(boolean b){
        monthComboBox.setVisible(b);
        yearComboBox.setVisible(b);
        monthLabel.setVisible(b);
        yearLabel.setVisible(b);
        dateError.setVisible(b);
    }
}
