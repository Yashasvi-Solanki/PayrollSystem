package com.payroll.db;

import com.payroll.util.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/payroll_db";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "your_password_here";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Logger.info("Database connection established.");
            } catch (ClassNotFoundException e) {
                Logger.error("MySQL JDBC Driver not found.", e);
                throw new SQLException("Driver not found", e);
            }
        }
        return connection;
    }

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
