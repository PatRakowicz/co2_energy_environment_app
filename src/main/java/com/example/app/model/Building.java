package com.example.app.model;

import java.util.Date;

public class Building {

    private int buildingID;
    private String name;
    private String location;
    private int sqFT;
    private Date date;
    private Date startShared;
    private Date endShared;

    // Getters
    public int getBuildingID() {
        return buildingID;
    }

    public String getName() {
        return name;
    }

    public Date getStartShared(){return startShared;}

    public Date getEndShared(){return endShared;}

    public String getLocation() {return location;}

    public int getSqFT() {return sqFT;}

    public Date getDate() {return date;}

    // Setters
    public void setBuildingID(int buildingID) {
        this.buildingID = buildingID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSqFT(int sqFT) {
        this.sqFT = sqFT;
    }

    public void setStartShared(Date startShared) {
        this.startShared = startShared;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEndShared(Date endShared) {this.endShared = endShared;}
}
