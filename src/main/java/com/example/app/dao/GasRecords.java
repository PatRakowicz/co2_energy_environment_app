package com.example.app.dao;

import com.example.app.model.Gas;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GasRecords implements DBQueries {
    private DBConn dbConn;
    private ArrayList<Gas> gasList = new ArrayList<>();
    private ResultSet resultSet;

    public GasRecords(DBConn dbConn) {
        this.dbConn = dbConn;
    }

    public ArrayList<Gas> getGas(int buildingID, LocalDate toBilling, LocalDate fromBilling, DBConn dbConn) {

        String query = "SELECT * FROM gas "
                + "WHERE buildingID = ? "
                + "AND to_billing >= ? AND from_billing <= ?";
        try (PreparedStatement statement = this.dbConn.getConnection().prepareStatement(query)) {
            statement.setInt(1, buildingID);
            statement.setDate(2, Date.valueOf(toBilling));
            statement.setDate(3, Date.valueOf(fromBilling));

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Gas gas = new Gas();

                gas.setBuildingID(resultSet.getInt("buildingID"));
                gas.setCurrentCharges(resultSet.getFloat("current_charges"));
                gas.setRate(resultSet.getString("rate"));
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
}
