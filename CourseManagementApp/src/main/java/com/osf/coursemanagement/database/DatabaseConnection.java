package com.osf.coursemanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=CourseManagement;encrypt=true;trustServerCertificate=true;";

    // ⚠️ AICI TREBUIE SĂ PUI USERUL ȘI PAROLA DE LA SQL SERVER-UL TĂU (Nu alea din tabela Users!)
    // De obicei userul default este "sa"
    private static final String USER = "app_user";
    private static final String PASSWORD = "Pass1234!";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexiune reusita la SQL!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Eroare la conectare:");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
