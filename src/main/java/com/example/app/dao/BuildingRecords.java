package com.example.app.dao;

import com.example.app.model.Building;

import java.sql.*;
import java.util.ArrayList;

public class BuildingRecords implements DBQueries {
    private DBConn dbConn;
    private ArrayList<Building> buildings = new ArrayList<>();
    private ResultSet resultSet;

    public BuildingRecords(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public boolean insertBuilding(Building building){
        String table = "building";
        String columns = "name, location, sqFT, date, start_shared, end_shared";

        String name, location, sqFT, date, start_shared, end_shared;
        name = "'" + building.getName() + "'";

        if(building.getLocation().isEmpty()){
            location = "NULL";
        }else{
            location = "'" + building.getLocation() + "'";
        }

        if(building.getSqFT() < 0){
            sqFT = "NULL";
        }else{
            sqFT = Integer.toString(building.getSqFT());
        }

        if(building.getDate() == null){
            date = "NULL";
        }else{
            date = String.format("'%s'", new Date(building.getDate().getTime()));
        }

        if(building.getStartShared() == null){
            start_shared = "NULL";
        }else{
            start_shared = String.format("'%s'", new Date(building.getStartShared().getTime()));
        }

        if(building.getEndShared() == null){
            end_shared = "NULL";
        }else{
            end_shared = String.format("'%s'", new Date(building.getEndShared().getTime()));
        }

        String values = String.format(
                "%s, %s, %s, %s, %s, %s",
                name,
                location,
                sqFT,
                date,
                start_shared,
                end_shared
        );

        return insert(table, columns, values, dbConn);
    }

    public ArrayList<Building> getBuildings() {
        Connection connection = dbConn.getConnection();
        if (connection == null) {
            System.out.println("No active database connection.");
            return buildings; // would return empty
        }

        String query = "SELECT * FROM building";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Building building = new Building();

                building.setBuildingID(resultSet.getInt("buildingID"));
                building.setName(resultSet.getString("name"));
                building.setLocation(resultSet.getString("location"));
                building.setSqFT(resultSet.getInt("sqFT"));
                building.setDate(resultSet.getDate("date"));
                building.setStartShared(resultSet.getDate("start_shared"));
                building.setEndShared(resultSet.getDate("end_shared"));

                buildings.add(building);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.printf("Caught SQL Error: %s", e);
        }

        return buildings;
    }
}
