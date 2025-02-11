package tn.esprit.tests;

import tn.esprit.utils.dbCon;

public class Main {
    public static void main(String[] args) {
        System.out.println(dbCon.getInstance().getConnection());
    }
}