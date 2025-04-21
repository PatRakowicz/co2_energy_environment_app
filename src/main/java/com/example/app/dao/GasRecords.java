package com.example.app.dao;

import com.example.app.model.Gas;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GasRecords implements DBQueries{
    private DBConn dbConn;
    private ArrayList<Gas> gasses = new ArrayList<>();
    private ResultSet resultSet;

    public GasRecords(DBConn c){this.dbConn = c;}

    public boolean insertGas(Gas gas){
        String table = "gas";
        String columns = "buildingID, current_charges, from_billing, to_billing, meter_read, billed_ccf";
        String values = String.format(
                "%d, %.2f, '%s', '%s', %.2f, %.2f",
                gas.getBuildingID(),
                gas.getCurrent_charges(),
                new Date(gas.getFrom_billing().getTime()),
                new Date(gas.getTo_billing().getTime()),
                gas.getMeter_read(),
                gas.getBilled_ccf()
        );
        return insert(table, columns, values, dbConn);
    }

    public ArrayList<Gas> getGasses(int buildingID, LocalDate from_billing, LocalDate to_billing, DBConn dbConn) throws SQLException {
        String table = "gas";
        String columns = "buildingID, current_charges, from_billing, to_billing, meter_read, billed_ccf";
        String condition = String.format(
                "buildingID = %d AND from_billing >= '%s' AND to_billing <= '%s'",
                buildingID, from_billing, to_billing);
        resultSet = read(table, columns, condition, dbConn);
        while (resultSet.next()){
            Gas gas = new Gas();

            gas.setBuildingID(resultSet.getInt("buildingID"));
            gas.setCurrent_charges(resultSet.getFloat("current_charges"));
            gas.setFrom_billing(resultSet.getDate("from_billing"));
            gas.setTo_billing(resultSet.getDate("to_billing"));
            gas.setMeter_read(resultSet.getFloat("meter_read"));
            gas.setBilled_ccf(resultSet.getFloat("billed_ccf"));
            gasses.add(gas);
        }
        return gasses;
    }
}
