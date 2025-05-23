package com.example.app.dao;

import com.example.app.model.Gas;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;

public class GasRecords implements DBQueries {
    private DBConn dbConn;
    private ArrayList<Gas> gasList = new ArrayList<>();
    private ResultSet resultSet;

    public GasRecords(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public boolean insertGas(Gas gas){
        String table = "gas";
        String columns = "buildingID, current_charges, from_billing, to_billing, meter_read, billed_ccf";
        String values = String.format(
                "%d, %.2f, '%s', '%s', %.2f, %.2f",
                gas.getBuildingID(),
                gas.getCurrentCharges(),
                new Date(gas.getFromBilling().getTime()),
                new Date(gas.getToBilling().getTime()),
                gas.getMeterRead(),
                gas.getBilledCCF()
        );
        return insert(table, columns, values, dbConn);
    }

    public ArrayList<Gas> getGas(int buildingID, LocalDate fromBilling, LocalDate toBilling, DBConn dbConn) {
        gasList.clear();

        String query = "SELECT * FROM gas "
                + "WHERE buildingID = ? "
                + "AND to_billing >= ? AND from_billing <= ?";
        try (PreparedStatement statement = this.dbConn.getConnection().prepareStatement(query)) {
            statement.setInt(1, buildingID);
            statement.setDate(2, Date.valueOf(fromBilling));
            statement.setDate(3, Date.valueOf(toBilling));

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Gas gas = new Gas();

                gas.setBuildingID(resultSet.getInt("buildingID"));
                gas.setCurrentCharges(resultSet.getFloat("current_charges"));
//                gas.setRate(resultSet.getString("rate"));
                gas.setToBilling(resultSet.getDate("to_billing"));
                gas.setFromBilling(resultSet.getDate("from_billing"));
                gas.setMeterRead(resultSet.getFloat("meter_read"));
                gas.setBilledCCF(resultSet.getFloat("billed_ccf"));

                gasList.add(gas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.printf("Caught SQL Error: %s", e);
        }

        return gasList;
    }

    public boolean findGas(int year, int month, int id){
        Connection connection = dbConn.getConnection();
        String query = String.format(
                "SELECT * FROM gas WHERE YEAR(to_billing) = %d AND MONTH(to_billing) = %d AND buildingID = %d",
                year, month, id);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            if (result != null && result.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
