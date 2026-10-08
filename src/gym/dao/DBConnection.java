package gym.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place that hands out JDBC connections.
 * Change URL / USER / PASSWORD to match your local MySQL setup.
 * Kept as a single class so the rest of the app never talks SQL config directly.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/gym_management?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "Hello";

    private static Connection connection;

    private DBConnection() { }  // prevent instantiation

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j jar to classpath.", e);
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
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
