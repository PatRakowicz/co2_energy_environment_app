package com.example.app.controllers.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Interface to call
// Will have Basic CRUD operations, but will provide with override for custom classes
public interface DBQueries {
    Connection getConnection();

    default boolean insert(String table, String columns, String values) {
        Connection connection = getConnection();
        if (connection == null) {
            System.out.println("No active database connection.");
            return false;
        }

        String query = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ")";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Need to add Read / Update / Delete
}
