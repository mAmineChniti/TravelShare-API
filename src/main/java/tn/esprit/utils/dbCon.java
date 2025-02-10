package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbCon {
    final String URL="jdbc:mysql://localhost:3306/travelshare";

    final String USERNAME="root";
    final String PASSWORD="";
    Connection connection;

    static dbCon instance;
    private dbCon(){
        try {
            connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static   dbCon getInstance(){
        if (instance==null){
            instance= new dbCon();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}