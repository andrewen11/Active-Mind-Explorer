package com.osf.coursemanagement.dao;

import com.osf.coursemanagement.database.DatabaseConnection;
import com.osf.coursemanagement.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User authenticate(String email, String password) {
        String query = "SELECT * FROM Users WHERE Email = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                //BINGO! User -> obj.
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("RoleType")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        //exemplu log
        System.out.println("Login admin...");
        User admin = dao.authenticate("andrei_balalau@osfdigital.com", "admin123Ha!Ha");

        if (admin != null) {
            System.out.println("Login reusit! Salut, " + admin.getFirstName() + ". Rol: " + admin.getRoleType());
        } else {
            System.out.println("Login eșuat! Email sau parola gresita!");
        }
    }
}
