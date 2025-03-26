package com.example.app.dao;

import com.example.app.controllers.DBController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Interface to call
// Will have Basic CRUD operations, but will provide with override for custom classes
public interface DBQueries {
    default boolean insert(String table, String columns, String values, DBController dbController) {
        Connection connection = dbController.getConnection();
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
    /* Example Use of insert
        DBController dbController = new DBController();
        DBQueries dbQueries = new DBQueries() {}; // Anonymous implementation
        boolean success = dbQueries.insert("users", "name, email", "'John Doe', 'john@example.com'", dbController);
    * */

    // Need to add Read / Update / Delete
    default ResultSet read(String table, String columns, String condition, DBController dbController) throws SQLException {
        Connection connection = dbController.getConnection();
        if (connection == null) {

            System.out.println("No active database connection.");
            return null;
        }

        String query = String.format("SELECT %s FROM %s", columns, table);
        if (!condition.isEmpty()) {
            condition = String.format(" WHERE %s", condition);
            query += condition;
        }
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return statement.executeQuery();
        } catch (SQLException e) {
            System.out.printf("Caught SQL Error: %s", e);
            return null;
        }
    }

    default boolean update(String table, String setClause, String condition, DBController dbController) {
        Connection connection = dbController.getConnection();
        if (connection == null) {
            System.out.println("No active db Connection.");
            return false;
        }
        String query = String.format("UPDATE %s SET %s", table, setClause);
        if (!condition.isEmpty()) {
            query += String.format(" WHERE %s", condition);
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows updated: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.printf("Caught SQL Error in update: %s%n", e);
            return false;
        }
    }
    /* Example Use of default update
     *  DBController dbController = new DBController();
     *  DBQueries dbQueries = new DBQueries() {};
     *
     *  boolean updated = dbQueries.update(
     *      "users",                                   // table
     *      "name='Jane Doe', email='jane@site.com'",  // SET clause
     *      "id = 1",                                  // WHERE clause
     *      dbController                               // connection provider
     *  );
     *  // This will build a SQL statement that would be ran as such
     *  // UPDATE users SET name='Jane Doe', email='jane@site.com' WHERE id = 1;
     *
     *  // Check if update was successful
     *  if (updated) { System.out.println("User updated successfully."); }
     *  else { System.out.println("No user was updated."); }
     * */

    default boolean delete(String table, String setClause, String condition, DBController dbController) {
        Connection connection = dbController.getConnection();
        if (connection == null) {
            System.out.println("No active db Connection.");
            return false;
        }

        // Safety to prevent full table wipe
        if (condition == null || condition.isEmpty()) {
            System.out.println("Delete condition is required to avoid removing all records!");
            return false;
        }

        String query = String.format("DELETE FROM %s WHERE %s", table, condition);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.printf("Caught SQL Error in delete: %s%n", e);
            return false;
        }
    }
    /* Example Use of default delete
     *  DBController dbController = new DBController();
     *  DBQueries dbQueries = new DBQueries() {}; // anonymous implementation
     *
     *  boolean deleted = dbQueries.delete(
     *      "users",          // table
     *      "id = 1",         // WHERE clause
     *      dbController      // connection provider
     *  );
     *
     *  // This will build and execute a SQL statement like:
     *  // DELETE FROM users WHERE id = 1;
     *
     *  // Check if deletion was successful
     *  if (deleted) { System.out.println("User deleted successfully."); }
     *  else { System.out.println("No user was deleted or user not found."); }
     * */
}
