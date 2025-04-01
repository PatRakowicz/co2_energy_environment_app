package com.example.app.model;

import java.util.Date;

public class Utility {

    private int utilityID;
    private int buildingID;
    private Date date;
    private float electricityCost;
    private float waterCost;
    private float sewageCost;
    private float miscCost;
    private float electricityUsage;
    private float waterUsage;
    private float usageGal;
    private float usageKwh;

    // Getters
    public Date getDate() {
        return date;
    }

    public float getElectricityCost() {
        return electricityCost;
    }

    public float getWaterCost() {
        return waterCost;
    }

    public float getSewageCost() {
        return sewageCost;
    }

    public float getMiscCost() {
        return miscCost;
    }

    public float getElectricityUsage() {
        return electricityUsage;
    }

    public float getWaterUsage() {
        return waterUsage;
    }

    public Object getBuildingID() {
        return buildingID;
    }

    // Setters
    public void setUtilityID(int utilityID) {
        this.utilityID = utilityID;
    }

    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setElectricityCost(float electricityCost) {
        this.electricityCost = electricityCost;
    }

    public void setWaterCost(float waterCost) {
        this.waterCost = waterCost;
    }

    public void setSewageCost(float sewageCost) {
        this.sewageCost = sewageCost;
    }

    public void setMiscCost(float miscCost) {
        this.miscCost = miscCost;
    }

    public void setElectricityUsage(float electricityUsage) {
        this.electricityUsage = electricityUsage;
    }

    public void setWaterUsage(float waterUsage) {
        this.waterUsage = waterUsage;
    }

    public void setUsageGal(float usageGal) {
        this.usageGal = usageGal;
    }

    public void setUsageKwh(float usageKwh) {
        this.usageKwh = usageKwh;
    }
}
