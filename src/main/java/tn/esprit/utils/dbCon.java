package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbCon {
    private static dbCon instance;
    private static final String URL = "jdbc:mysql://localhost:3306/TravelShare";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");
    private final Connection con;

    private dbCon() {
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("we hacked the mainframe");
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