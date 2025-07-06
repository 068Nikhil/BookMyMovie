package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConfig {
    static String url = "jdbc:mysql://localhost:3306/bookmymovie";
    static String user = "root";
    static String pass = "System01@sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
