package com.example.app.dao;

import com.example.app.model.Building;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuildingRecords implements DBQueries {
    private DBConn dbConn;
    private ArrayList<Building> buildings = new ArrayList<>();
    private ResultSet resultSet;

    public BuildingRecords(DBConn dbConn) {
        this.dbConn = dbConn;
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
