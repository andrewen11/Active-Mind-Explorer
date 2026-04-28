package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.HelloApplication;
import com.osf.coursemanagement.dao.UserDAO;
import com.osf.coursemanagement.model.User;
import com.osf.coursemanagement.model.UserSession;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblErrorMessage;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLoginButtonAction() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // VALIDARE CAMPURI GOALE
        if (email.isEmpty() && password.isEmpty()) {
            lblErrorMessage.setText("Please complete all the fields!");
            return;
        } else if (email.isEmpty()) {
            lblErrorMessage.setText("Please enter the email address!");
            return;
        } else if (password.isEmpty()) {
            lblErrorMessage.setText("Please enter the password!");
            return;
        }

        // AUTENTIFICARE
        User authenticatedUser = userDAO.authenticate(email, password);

        if (authenticatedUser != null) {
            UserSession.getInstance().setLoggedInUser(authenticatedUser);

            if (authenticatedUser.isAdmin() || "Collaborator".equals(authenticatedUser.getRoleType())) {
                lblErrorMessage.setText("Authentification successful! Welcome, " + authenticatedUser.getFirstName()+ " " + authenticatedUser.getLastName() + "!");
                System.out.println("ACCESS GRANTED. Loading main dashboard...");
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("dashboard-view.fxml"));
                        Parent root = loader.load();

                        Stage stage = (Stage) btnLogin.getScene().getWindow();

                        stage.setScene(new Scene(root));
                        stage.setTitle("Course Management - Dashboard");
                        stage.show();

                    } catch (IOException e) {
                        lblErrorMessage.setText("Could not load application dashboard.");
                        e.printStackTrace();
                    }
                });
                pause.play();
                // TODO: tranzitie + close login window
            } else {
                lblErrorMessage.setText("Access denied. Only Admins/Staff can use this console.");
                UserSession.getInstance().cleanUserSession();
            }
        } else {
            lblErrorMessage.setText("Invalid email or password! Try again!");
        }
    }
}
