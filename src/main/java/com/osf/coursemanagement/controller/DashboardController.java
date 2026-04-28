/**
 * Clasa pentru controller-ul ferestrei principale (Dashboard)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.HelloApplication;
import com.osf.coursemanagement.dao.CourseDAO;
import com.osf.coursemanagement.dao.UserDAO;
import com.osf.coursemanagement.model.User;
import com.osf.coursemanagement.model.UserSession;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;
import java.util.function.Consumer;

public class DashboardController {

	@FXML
	private Label lblGreeting;
	@FXML
	private Label lblPlans;
	@FXML
	private Label lblRole;
	@FXML
	private ImageView imgGreeting;
	@FXML
	private Button btnLogout;
	@FXML
	private ImageView imgHeaderLogo;
	@FXML
	private Button btnThemeToggle;
	@FXML
	private BorderPane dashboardRoot;

	// containere panouri
	@FXML
	private VBox panelUsers;
	@FXML
	private VBox panelCourses;
	@FXML
	private VBox panelModules;
	@FXML
	private VBox panelProgress;

	// iconite panouri
	@FXML
	private ImageView iconUsers;
	@FXML
	private ImageView iconCourses;
	@FXML
	private ImageView iconModules;
	@FXML
	private ImageView iconProgress;

	private final CourseDAO courseDAO = new CourseDAO();
	private final UserDAO userDAO = new UserDAO();
	private final User loggedInUser = UserSession.getInstance().getLoggedInUser();
	private boolean isDarkMode = false;

	public void initialize() {
		if (loggedInUser != null) {
			setupGreeting();
			applyRBAC();
			setTheme(UserSession.getInstance().isDarkMode());
		}
		bindScaling(); // verificari scalare ui
	}

	private void bindScaling() {
		// inaltime img (30% relativ)
		imgGreeting.fitHeightProperty().bind(dashboardRoot.heightProperty().multiply(0.3));
		imgHeaderLogo.fitHeightProperty().bind(dashboardRoot.heightProperty().multiply(0.3));

		// latimi panouri
		javafx.beans.value.ObservableValue<? extends Number> panelWidth = dashboardRoot.widthProperty().multiply(0.22);
		panelUsers.prefWidthProperty().bind(panelWidth);
		panelCourses.prefWidthProperty().bind(panelWidth);
		panelModules.prefWidthProperty().bind(panelWidth);
		panelProgress.prefWidthProperty().bind(panelWidth);

		// dim iconite
		javafx.beans.value.ObservableValue<? extends Number> iconSize = panelUsers.widthProperty().multiply(0.4);
		iconUsers.fitWidthProperty().bind(iconSize);
		iconUsers.fitHeightProperty().bind(iconSize);
		iconCourses.fitWidthProperty().bind(iconSize);
		iconCourses.fitHeightProperty().bind(iconSize);
		iconModules.fitWidthProperty().bind(iconSize);
		iconModules.fitHeightProperty().bind(iconSize);
		iconProgress.fitWidthProperty().bind(iconSize);
		iconProgress.fitHeightProperty().bind(iconSize);

		// scalare dinamica font (pt flexibilitate la ferestre de dim dif - nu se
		// respecta pt <paneluri>!!!)
		lblGreeting.styleProperty()
				.bind(Bindings.concat("-fx-font-size: ", dashboardRoot.widthProperty().divide(30).asString(), "px;"));
		lblPlans.styleProperty()
				.bind(Bindings.concat("-fx-font-size: ", dashboardRoot.widthProperty().divide(45).asString(), "px;"));
		lblRole.styleProperty()
				.bind(Bindings.concat("-fx-font-size: ", dashboardRoot.widthProperty().divide(47).asString(), "px;"));
	}

	// setup greeting text + img modificata (calea rel) in functie de ora curenta
	private void setupGreeting() {
		LocalTime now = LocalTime.now();
		String timeGreeting;
		String imagePath;

		if (now.isBefore(LocalTime.of(6, 0))) {
			timeGreeting = "Good Night";
			imagePath = "/com/osf/coursemanagement/images/icon_night.png";
		} else if (now.isBefore(LocalTime.NOON)) {
			timeGreeting = "Good Morning";
			imagePath = "/com/osf/coursemanagement/images/icon_morning.png";
		} else if (now.isBefore(LocalTime.of(18, 0))) {
			timeGreeting = "Good Afternoon";
			imagePath = "/com/osf/coursemanagement/images/icon_noon.png";
		} else if (now.isBefore(LocalTime.of(22, 0))) {
			timeGreeting = "Good Evening";
			imagePath = "/com/osf/coursemanagement/images/icon_evening.png";
		} else {
			timeGreeting = "Good Night";
			imagePath = "/com/osf/coursemanagement/images/icon_night.png";
		}

		lblGreeting.setText(timeGreeting + ", " + loggedInUser.getFirstName() + "!");
		String roleText = (loggedInUser.getRoleType() != null) ? loggedInUser.getRoleType().toUpperCase() : "TRAINEE";
		lblRole.setText(roleText);

		try {
			if (getClass().getResource(imagePath) != null) {
				imgGreeting.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void applyRBAC() {
		String role = loggedInUser.getRoleType();
		boolean isAdmin = "Admin".equalsIgnoreCase(role);
		boolean isTrainer = "Trainer".equalsIgnoreCase(role);
		panelUsers.setVisible(false);
		panelUsers.setManaged(false);
		panelCourses.setVisible(false);
		panelCourses.setManaged(false);
		panelModules.setVisible(false);
		panelModules.setManaged(false);

		if (isAdmin) {
			panelUsers.setVisible(true);
			panelUsers.setManaged(true);
			panelCourses.setVisible(true);
			panelCourses.setManaged(true);
			panelModules.setVisible(true);
			panelModules.setManaged(true);
		} else if (isTrainer) {
			panelCourses.setVisible(true);
			panelCourses.setManaged(true);
			panelModules.setVisible(true);
			panelModules.setManaged(true);
		}
	}

	// navigare + actiuni
	@FXML
	public void goToManageUsers() {
		loadView("/com/osf/coursemanagement/users-view.fxml", null);
	}

	@FXML
	public void goToManageCourses() {
		loadView("/com/osf/coursemanagement/courses-view.fxml", controller -> {
			if (controller instanceof CourseManagementController) //
			{
				CourseManagementController cmc = (CourseManagementController) controller;

				Stage currentStage = (Stage) dashboardRoot.getScene().getWindow();
				// folosesc lambda ca sa definesc ce se intampla cand dau back din
				// celalalt controller-adica revin la scena dashboard fara sa o recreez (*)
				cmc.setOnBack(() -> {
					restoreState();
					currentStage.setScene(dashboardRoot.getScene());
				});
			}
		});
	}

	@FXML
	public void goToManageModules() {
		loadView("/com/osf/coursemanagement/module-management-view.fxml", controller -> {
			if (controller instanceof ModuleManagementController) {
				ModuleManagementController mmc = (ModuleManagementController) controller; // vezi(*)
				// e aceeasi logica/ matrita pt leg dashboard - paneluri
				Stage currentStage = (Stage) dashboardRoot.getScene().getWindow();
				mmc.setOnBack(() -> {
					restoreState();
					currentStage.setScene(dashboardRoot.getScene());
				});
			}
		});
	}

	@FXML
	public void handleLiveAttendance() {
		loadView("/com/osf/coursemanagement/live-attendance-view.fxml", null);
	}

	@FXML
	public void handleBadgeProgress() {
		loadView("/com/osf/coursemanagement/badges-view.fxml", null);
	}

	// helper generic pt incarcare viewuri/ schimbare scena
	private <T> void loadView(String fxmlPath, Consumer<T> initializer) {
		try {
			double width = dashboardRoot.getScene().getWidth();
			double height = dashboardRoot.getScene().getHeight();

			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Scene scene = new Scene(loader.load(), width, height);

			if (initializer != null) {
				initializer.accept(loader.getController());
			}

			Stage stage = (Stage) dashboardRoot.getScene().getWindow();
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("Error", "Could not load view: " + fxmlPath);
		}
	}

	// handlere pt statistici (a se vedea metoda loadView )
	@FXML
	public void handleStatsUsers() {
		var topStudents = userDAO.getTopStudents();
		StringBuilder sb = new StringBuilder("--- TOP STUDENTS (>1 Enrolments) ---\n");
		if (topStudents.isEmpty())
			sb.append("None found.\n");
		else
			topStudents.forEach(
					u -> sb.append("- ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n"));
		// pt a vedea in dialog (alert panel) **
		showAlert("User Statistics", sb.toString());
	}

	@FXML
	public void handleStatsCourses() {
		var abandoned = courseDAO.getAbandonedCourses();
		StringBuilder sb = new StringBuilder("Abandoned Courses (No Students) \n");
		if (abandoned.isEmpty())
			sb.append("None. Good job!\n");
		else
			abandoned.forEach(c -> sb.append("- ").append(c.getName()).append("\n"));
		// vezi(**)
		showAlert("Course Statistics", sb.toString());
	}

	@FXML
	public void handleStatsModules() {
		var bigCourses = courseDAO.getBigCourses(2);
		StringBuilder sb = new StringBuilder("Courses with many modules (> 2) \n");
		if (bigCourses.isEmpty())
			sb.append("None found.\n");
		else
			bigCourses.forEach(c -> sb.append("- ").append(c.getName()).append("\n"));
		// vezi(**)
		showAlert("Module Statistics", sb.toString());
	}

	@FXML
	public void handleStatsProgress() {
		var active = userDAO.getStudentsWithLiveAttendance();
		StringBuilder sb = new StringBuilder("Students with live attendance: \n");
		if (active.isEmpty())
			sb.append("No attendance records found.\n");
		else
			active.forEach(
					u -> sb.append("- ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n"));
		showAlert("Progress Statistics", sb.toString());
	}

	// pt pastrarea logicii temei (dark / light) si logout (a se vedea si setTheme)
	@FXML
	private void handleThemeToggle() {
		boolean newMode = !UserSession.getInstance().isDarkMode(); // inversare modului (retinerea starii)
		UserSession.getInstance().setDarkMode(newMode);
		setTheme(newMode); // aplicare
	}

	public void restoreState() {
		setTheme(UserSession.getInstance().isDarkMode());
	}

	private void setTheme(boolean dark) {
		this.isDarkMode = dark;
		String logoPath; // pt logo (dark / light in functie de tema <- img dif / cai relative diferiote)
							// (***)
		if (isDarkMode) {
			if (!dashboardRoot.getStyleClass().contains("dark-theme")) {
				dashboardRoot.getStyleClass().add("dark-theme");
			}
			btnThemeToggle.setText("Light Mode");
			logoPath = "/com/osf/coursemanagement/images/logo_darkmode.png"; // (***)
		} else {
			dashboardRoot.getStyleClass().remove("dark-theme");
			btnThemeToggle.setText("Dark Mode");
			logoPath = "/com/osf/coursemanagement/images/logo.png"; // (***)
		}

		try {
			if (getClass().getResource(logoPath) != null) {
				imgHeaderLogo.setImage(new Image(getClass().getResource(logoPath).toExternalForm())); // setare logo AME
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleLogout() {
		UserSession.getInstance().cleanUserSession(); // eliminare user din sesiune si intoarcere la login page
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), 600, 400);
			Stage stage = (Stage) btnLogout.getScene().getWindow();
			stage.setTitle("Active Mind Explorer - Authentication");
			stage.setScene(scene);
			stage.centerOnScreen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// helper pt conf de alert dialogs
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		try {
			String css = getClass().getResource("/com/osf/coursemanagement/style.css").toExternalForm(); // o parte din
																											// stil se
																											// regaseste
																											// in css ul
																											// comun!
			alert.getDialogPane().getStylesheets().add(css);
			if (UserSession.getInstance().isDarkMode()) {
				alert.getDialogPane().getStyleClass().add("dark-theme");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		alert.showAndWait();
	}
}