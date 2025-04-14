package com.example.app.model;

import java.util.Date;

public class Utility {

    private int utilityID;
    private int buildingID;
    private Date date;
    private Float electricityCost;
    private Float waterCost;
    private Float sewageCost;
    private Float miscCost;
    private Float electricityUsage;
    private Float waterUsage;
    private Float usageGal;
    private Float usageKwh;

    // Getters
    public Date getDate() {
        return date;
    }

    public Float getElectricityCost() {
        return electricityCost;
    }

    public Float getWaterCost() {
        return waterCost;
    }

    public Float getSewageCost() {
        return sewageCost;
    }

    public Float getMiscCost() {
        return miscCost;
    }

    public Float getElectricityUsage() {
        return electricityUsage;
    }

    public Float getWaterUsage() {
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

    public void setElectricityCost(Float electricityCost) {
        this.electricityCost = electricityCost;
    }

    public void setWaterCost(Float waterCost) {
        this.waterCost = waterCost;
    }

    public void setSewageCost(Float sewageCost) {
        this.sewageCost = sewageCost;
    }

    public void setMiscCost(Float miscCost) {
        this.miscCost = miscCost;
    }

    public void setElectricityUsage(Float electricityUsage) {
        this.electricityUsage = electricityUsage;
    }

    public void setWaterUsage(Float waterUsage) {
        this.waterUsage = waterUsage;
    }

    public void setUsageGal(Float usageGal) {
        this.usageGal = usageGal;
    }

    public void setUsageKwh(Float usageKwh) {
        this.usageKwh = usageKwh;
    }
}
