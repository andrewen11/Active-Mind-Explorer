/**
 * Clasa pentru punctul de intrare in aplicatia JavaFX
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		// incarc interfata de login din fxml
		FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
		// setarea ferestrei (prima - login)
		Scene scene = new Scene(fxmlLoader.load(), 720, 560);
		stage.setTitle("Active Mind Explorer - Login");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}