package com.example.app.model;

import java.util.Date;

public class Gas {
    private int buildingID;
    private double currentCharges;
    private String rate;
    private Date fromBilling;
    private Date toBilling;
    private double meterRead;
    private double billedCCF;

    // Getters and Setters for billed_ccf
    public double getBilledCCF() {
        return billedCCF;
    }

    public void setBilledCCF(double billedCCF) {
        this.billedCCF = billedCCF;
    }

    // Getters and Setters for meter_read
    public double getMeterRead() {
        return meterRead;
    }

    public void setMeterRead(double meterRead) {
        this.meterRead = meterRead;
    }

    // Getters and Setters for to_billing
    public Date getToBilling() {
        return toBilling;
    }

    public void setToBilling(Date toBilling) {
        this.toBilling = toBilling;
    }

    // Getters and Setters for from_building
    public Date getFromBilling() {
        return fromBilling;
    }

    public void setFromBilling(Date fromBilling) {
        this.fromBilling = fromBilling;
    }

    // Getters and Setters for rate
    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    // Getters and Setters for current_charges
    public double getCurrentCharges() {
        return currentCharges;
    }

    public void setCurrentCharges(double currentCharges) {
        this.currentCharges = currentCharges;
    }

    // Getters and Setters for buildingID
    public int getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }
}
