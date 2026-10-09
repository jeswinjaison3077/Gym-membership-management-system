package gym.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ============================================================================
 * [CONCEPT: JDBC DATABASE CONNECTION]
 * Manages database connectivity using JDBC (Java Database Connectivity).
 * 
 * Demonstrates:
 * 1. JDBC Driver Loading (com.mysql.cj.jdbc.Driver)
 * 2. DriverManager connection initialization
 * 3. Singleton pattern concept for managing database connection instance
 * ============================================================================
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/gym_management?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "Hello";

    private static Connection connection;

    private DBConnection() { }  // Private constructor prevents instantiation

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Ensure MySQL Connector JAR is in classpath.", e);
            }
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing DB connection: " + e.getMessage());
        }
    }
}
