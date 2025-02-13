package tn.esprit.utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbCon {
    private static dbCon instance;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:mysql://localhost:3306/travelshare";
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");
    private final Connection con;

    private dbCon() {
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static dbCon getInstance() {
        if (instance == null) {
            instance = new dbCon();
        }
        return instance;
    }

    public Connection getConnection() {
        return con;
    }
}