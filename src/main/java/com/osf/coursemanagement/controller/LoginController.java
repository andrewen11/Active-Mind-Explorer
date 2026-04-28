/**
 * Clasa pentru controller-ul ferestrei de autentificare
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.HelloApplication;
import com.osf.coursemanagement.dao.UserDAO;
import com.osf.coursemanagement.model.User;
import com.osf.coursemanagement.model.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

	@FXML
	private ImageView logoImage;
	@FXML
	private TextField txtEmail;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Button btnLogin;
	@FXML
	private Label lblErrorMessage;

	private final UserDAO userDAO = new UserDAO();

	@FXML
	private VBox loginRoot;

	@FXML
	public void initialize() {
		// binding simplu - <<fixez>> direct pe dimensiunile containerului-radacina
		logoImage.fitWidthProperty().bind(loginRoot.widthProperty().multiply(0.6));
	}

	@FXML
	private void handleLoginButtonAction() {
		String email = txtEmail.getText().trim();
		String password = txtPassword.getText().trim();

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

		try {
			User authenticatedUser = userDAO.authenticate(email, password);

			if (authenticatedUser != null) {
				// asigur ca parola e setata pt basic auth (serverul poate nu o returneaza)
				authenticatedUser.setPassword(password);
				UserSession.getInstance().setLoggedInUser(authenticatedUser);
				loadSplashScreen(authenticatedUser);
			}
		} catch (RuntimeException e) {
			// afisez mesajul venit direct de la server (via UserDAO)
			lblErrorMessage.setText(e.getMessage());
		} catch (IOException e) {
			lblErrorMessage.setText("Server connection failed! Is the backend running?");
			e.printStackTrace();
		} catch (InterruptedException e) {
			lblErrorMessage.setText("Login interrupted.");
			e.printStackTrace();
		}
	}

	private void loadSplashScreen(User user) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("splash-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 600, 500);

		SplashController controller = fxmlLoader.getController();
		controller.setLoggedInUser(user);

		Stage stage = (Stage) btnLogin.getScene().getWindow();
		stage.setTitle("Authentication Successful");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}
}
