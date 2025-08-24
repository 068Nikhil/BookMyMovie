package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConfig {
    static String url = "jdbc:mysql://localhost:3306/bookmymovie";
    static String user = System.getenv("DB_USER");
    static String pass = System.getenv("DB_PASS");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
