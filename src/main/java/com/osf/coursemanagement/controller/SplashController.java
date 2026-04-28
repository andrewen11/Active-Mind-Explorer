/**
 * Clasa pentru controller-ul ecranului de pornire (Splash Screen)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.HelloApplication;
import com.osf.coursemanagement.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class SplashController {
	@FXML
	private ImageView splashImage;
	@FXML
	private Label lblWelcome;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private VBox splashRoot;
	@FXML
	private VBox splashCard;

	// init card animatie autentificare
	public void initialize() {
		javafx.scene.transform.Scale scale = new javafx.scene.transform.Scale();
		scale.xProperty().bind(splashRoot.widthProperty().divide(800.0));
		scale.yProperty().bind(splashRoot.widthProperty().divide(800.0));
		scale.setPivotX(200); // centrul aprox al cardului de 400px
		scale.setPivotY(200);

		splashCard.getTransforms().add(scale);
	}

	public void setLoggedInUser(User user) {
		// msj de bun venit personalizat pt userul logat
		String baseMessage = "Welcome, " + user.getFirstName() + " " + user.getLastName()
				+ "!";
		lblWelcome.setText(baseMessage);
		if (progressBar != null) {
			progressBar.setProgress(0.0);
		}

		// aici am simulat o tentativa de incarcare date cu timeline si progressbar /
		// animatie loading (aprox la fiecare secunda)
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.seconds(0.5), e -> {
					lblWelcome.setText(baseMessage + "\n\nInitialization...");
					if (progressBar != null)
						progressBar.setProgress(0.1); // actualizez progres vizual -la fiecare secunda se coloreaza lin
				}),
				new KeyFrame(Duration.seconds(1.5), e -> {
					lblWelcome.setText(baseMessage + "\n\nLoading User Profile...");
					if (progressBar != null)
						progressBar.setProgress(0.3);
				}),
				new KeyFrame(Duration.seconds(2.5), e -> {
					lblWelcome.setText(baseMessage + "\n\nConnecting to Database...");
					if (progressBar != null)
						progressBar.setProgress(0.5);
				}),
				new KeyFrame(Duration.seconds(3.5), e -> {
					lblWelcome.setText(baseMessage + "\n\nFetching Courses...");
					if (progressBar != null)
						progressBar.setProgress(0.8);
				}),
				new KeyFrame(Duration.seconds(4.5), e -> {
					lblWelcome.setText(baseMessage + "\n\nFinalizing Setup...");
					if (progressBar != null)
						progressBar.setProgress(1.0);
				}),
				new KeyFrame(Duration.seconds(5.5), e -> loadDashboard())); // la final incarc dashboard
		timeline.play(); // pornesc animatia
	}

	// config fereastra dashboard
	private void loadDashboard() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("dashboard-view.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), 800, 600); // setez dim fixa la fereastra principala

			if (lblWelcome.getScene() != null) {
				Stage stage = (Stage) lblWelcome.getScene().getWindow();
				stage.setTitle("Active Mind Explorer - Dashboard");
				stage.setScene(scene);
				stage.centerOnScreen();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
