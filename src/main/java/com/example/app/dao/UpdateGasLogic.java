package com.example.app.dao;

import com.example.app.model.Building;
import com.example.app.model.Gas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateGasLogic implements DBQueries{
    private final DBConn dbConn;

    public UpdateGasLogic(DBConn c){this.dbConn = c;}

    public Gas getGasForDate(int buildingID, int year, int month){
        Gas gas = new Gas();
        Connection connection = dbConn.getConnection();
        if(connection == null){
            return null;
        }
        String table = "FROM gas ";
        String columns = "SELECT gasID, buildingID, current_charges, from_billing, to_billing, meter_read, billed_ccf ";
        String conditions = String.format(
                "WHERE YEAR(to_billing) = %d AND MONTH(to_billing) = %d AND buildingID = %d",
                year, month, buildingID);
        String query = columns + table + conditions;
        try (PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet resultSet = statement.executeQuery();

            if (resultSet != null && resultSet.next()) {
                gas.setGasID(resultSet.getInt("gasID"));
                gas.setBuildingID(resultSet.getInt("buildingID"));
                gas.setCurrent_charges(resultSet.getFloat("current_charges"));
                gas.setFrom_billing(resultSet.getDate("from_billing"));
                gas.setTo_billing(resultSet.getDate("to_billing"));
                gas.setMeter_read(resultSet.getFloat("meter_read"));
                gas.setBilled_ccf(resultSet.getFloat("billed_ccf"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return gas;
    }

    public boolean updateGas(Building building, Gas gas){
        if (building == null || building.getBuildingID() == 0 || gas.getTo_billing() == null) {
            System.out.println("Invalid input for updateUtility.");
            return false;
        }
        String table = "gas";
        String setClause = String.format(
                "current_charges = %.2f, from_billing = '%s', to_billing = '%s', meter_read = %.2f, billed_ccf = %.2f",
                gas.getCurrent_charges(),
                gas.getFrom_billing(),
                gas.getTo_billing(),
                gas.getMeter_read(),
                gas.getBilled_ccf()
                );
        String condition = String.format("gasID = %d", gas.getGasID());
        return update(table, setClause, condition, dbConn);
    }

    public int getMinGasYear(){
        String query = "SELECT MIN(YEAR(to_billing)) AS min_year FROM gas";
        try (PreparedStatement stmt = dbConn.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int year = rs.getInt("min_year");
                return rs.wasNull() ? LocalDate.now().getYear() : year;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching min year: " + e.getMessage());
        }
        return LocalDate.now().getYear();
    }

    public boolean deleteGas(Gas gas){
        String table = "gas";
        String condition = String.format("gasID = %d", gas.getGasID());
        return delete(table, condition, dbConn);
    }
}
