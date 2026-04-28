package com.payroll.db;

import com.payroll.util.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class managing the JDBC connection to MySQL.
 *
 * Demonstrates:
 *  - JDBC connection setup
 *  - Singleton design pattern
 *  - try-with-resources compatibility
 *
 * ⚠️ SETUP REQUIRED:
 *  1. Install MySQL 8.x
 *  2. Create database: CREATE DATABASE payroll_db;
 *  3. Update DB_URL, DB_USER, DB_PASSWORD below
 *  4. Add mysql-connector-j.jar to lib/ folder and classpath
 */
public class DBConnection {

    // ─── Configure these before running ───────────────────────────────────────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/payroll_db";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "your_password_here";
    // ──────────────────────────────────────────────────────────────────────────

    private static Connection connection = null;

    /**
     * Returns the singleton JDBC Connection instance.
     * Creates a new connection if one doesn't exist or is closed.
     *
     * @return Active JDBC Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Logger.info("Database connection established.");
            } catch (ClassNotFoundException e) {
                Logger.error("MySQL JDBC Driver not found. Add mysql-connector-j.jar to lib/", e);
                throw new SQLException("Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Closes the database connection.
     * Call this when the application exits.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                Logger.info("Database connection closed.");
            } catch (SQLException e) {
                Logger.error("Error closing database connection", e);
            }
        }
    }
}
