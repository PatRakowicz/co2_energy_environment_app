package com.example.app.model;

import java.sql.Date;

public class Gas {
    private int gasID;
    private int buildingID;
    private float current_charges;
    private String rate;
    private Date from_billing;
    private Date to_billing;
    private float meter_read;
    private float billed_ccf;

    //getters

    public int getGasID() {return gasID;}

    public int getBuildingID() {return buildingID;}

    public float getCurrent_charges() {return current_charges;}

    public String getRate() {return rate;}

    public Date getFrom_billing() {return from_billing;}

    public Date getTo_billing() {return to_billing;}

    public float getMeter_read() {return meter_read;}

    public float getBilled_ccf() {return billed_ccf;}

    //setters

    public void setGasID(int gasID) {this.gasID = gasID;}

    public void setBuildingID(int buildingID) {this.buildingID = buildingID;}

    public void setCurrent_charges(float current_charges) {this.current_charges = current_charges;}

    public void setRate(String rate) {this.rate = rate;}

    public void setFrom_billing(Date from_billing) {this.from_billing = from_billing;}

    public void setTo_billing(Date to_billing) {this.to_billing = to_billing;}

    public void setMeter_read(float meter_read) {this.meter_read = meter_read;}

    public void setBilled_ccf(float billed_ccf) {this.billed_ccf = billed_ccf;}
}
