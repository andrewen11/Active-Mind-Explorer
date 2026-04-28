package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.model.User;
import com.osf.coursemanagement.model.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblWelcomeMessage;
    @FXML private Button btnLogout;
    @FXML
    public void initialize() {
        User currentUser = UserSession.getInstance().getLoggedInUser();

        if (currentUser != null) {
            lblWelcomeMessage.setText("SQL CONNECTION SUCCESFUL! Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
        } else {
            lblWelcomeMessage.setText("Error: User session not found.");
        }

        // dezactivare a butoanelor pentru Collab
    }

    @FXML
    public void handleLogout() {
        UserSession.getInstance().cleanUserSession();

        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }
}