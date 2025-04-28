package com.example.app.model;

import java.time.LocalDate;
import java.sql.Date;

public class Gas {
    private int buildingID;
    private int gasID;
    private Float currentCharges;
    private String rate;
    private Date fromBilling;
    private Date toBilling;
    private Float meterRead;
    private Float billedCCF;

    public int getGasID() {
        return gasID;
    }

    public void setGasID(int gasID) {
        this.gasID = gasID;
    }

    // Getters and Setters for billed_ccf
    public Float getBilledCCF() {
        return billedCCF;
    }

    public void setBilledCCF(Float billedCCF) {
        this.billedCCF = billedCCF;
    }

    // Getters and Setters for meter_read
    public Float getMeterRead() {
        return meterRead;
    }

    public void setMeterRead(Float meterRead) {
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
    public Float getCurrentCharges() {
        return currentCharges;
    }

    public void setCurrentCharges(Float currentCharges) {
        this.currentCharges = currentCharges;
    }

    // Setter for buildingID
    public int getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }
}
