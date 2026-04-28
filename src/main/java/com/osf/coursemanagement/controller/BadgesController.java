/**
 * Clasa pentru controller-ul gestionarii insignelor (Badges)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.dao.CourseDAO;
import com.osf.coursemanagement.dao.EnrolmentDAO;
import com.osf.coursemanagement.dao.ModuleDAO;
import com.osf.coursemanagement.dao.ProgressDAO;
import com.osf.coursemanagement.dao.UserDAO;
import com.osf.coursemanagement.model.Course;
import com.osf.coursemanagement.model.Module;
import com.osf.coursemanagement.model.SelfPacedProgress;
import com.osf.coursemanagement.model.User;
import com.osf.coursemanagement.model.UserSession;

import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BadgesController {

	@FXML
	private BorderPane rootPane;
	@FXML
	private ComboBox<Course> comboCourses;
	@FXML
	private ListView<TraineeItem> listTrainees;
	@FXML
	private VBox detailsContainer;
	@FXML
	private Label lblPlaceholder;
	@FXML
	private Label lblTraineeName;
	@FXML
	private Label lblTotalBadges;
	@FXML
	private Label lblTotalScore;
	@FXML
	private VBox modulesContainer;
	@FXML
	private Button btnThemeToggle;
	@FXML
	private Button btnAddStudent;
	@FXML
	private Button btnRemoveStudent;

	private final CourseDAO courseDAO = new CourseDAO();
	private final ModuleDAO moduleDAO = new ModuleDAO();
	private final ProgressDAO progressDAO = new ProgressDAO();
	private final EnrolmentDAO enrolmentDAO = new EnrolmentDAO();
	private final UserDAO userDAO = new UserDAO();

	// mapare: moduleid -> textfield pt input puncte
	private final Map<Integer, TextField> scoreInputs = new HashMap<>();
	// mapare: moduleid -> progressid (daca exista)
	private final Map<Integer, Integer> progressIds = new HashMap<>();

	private List<Module> currentModules = new ArrayList<>();
	private TraineeItem selectedTrainee;

	@FXML
	public void initialize() {
		// incarc cursuri
		List<Course> courses = courseDAO.getAllCourses();
		comboCourses.setItems(FXCollections.observableArrayList(courses));

		// listeners care sunt apelate/ "declansate atunci cand sunt selectate cursuri
		// sau studenti
		comboCourses.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
			if (newV != null)
				loadTrainees(newV);
		});

		listTrainees.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
			if (newV != null)
				showDetails(newV);
			else
				hideDetails();
		});

		// tema
		setTheme(UserSession.getInstance().isDarkMode());
	}

	private void loadTrainees(Course course) {
		listTrainees.getItems().clear();
		hideDetails();

		List<Map<String, Object>> participants = courseDAO.getParticipants(course.getId());

		List<TraineeItem> items = participants.stream()
				.map(p -> new TraineeItem(
						(String) p.get("studentName"),
						(Integer) p.get("enrolmentId"),
						(Integer) p.get("userId")))
				.collect(Collectors.toList());

		listTrainees.setItems(FXCollections.observableArrayList(items));
	}

	private void showDetails(TraineeItem trainee) {
		selectedTrainee = trainee;
		detailsContainer.setVisible(true);
		lblPlaceholder.setVisible(false);

		lblTraineeName.setText(trainee.name);

		Course selectedCourse = comboCourses.getValue();
		if (selectedCourse != null) {
			loadModulesAndProgress(selectedCourse.getId(), trainee.enrolmentId);
		}
	}

	private void hideDetails() {
		detailsContainer.setVisible(false);
		lblPlaceholder.setVisible(true);
		selectedTrainee = null;
	}

	private void loadModulesAndProgress(int courseId, int enrolmentId) {
		modulesContainer.getChildren().clear();
		scoreInputs.clear();
		progressIds.clear();

		// preiau module
		currentModules = moduleDAO.getModulesByCourseId(courseId);

		// preiau progres
		List<SelfPacedProgress> progressList = progressDAO.getProgressByEnrolmentId(enrolmentId);
		Map<Integer, SelfPacedProgress> progressMap = progressList.stream()
				.collect(Collectors.toMap(SelfPacedProgress::getModuleId, p -> p));

		int totalScore = 0;
		int totalBadges = 0;

		for (Module m : currentModules) {
			SelfPacedProgress p = progressMap.get(m.getModuleId());
			int currentScorePercent = 0; // interval pct scalat la 0-100
			int currentPoints = 0;

			if (p != null) {
				currentScorePercent = p.getScore();
				// convertesc % inapoi in puncte: (procent * max) / 100
				currentPoints = (int) Math.round((currentScorePercent * m.getNumberOfPoints()) / 100.0);
				progressIds.put(m.getModuleId(), p.getProgressId());
			}

			totalScore += currentScorePercent; // puncte obtinute din module

			// logica insigne
			// standard: scor > 0 -> 1 insigna
			// suprbadge: scor >= 50% -> 3 insigne
			if (currentScorePercent > 0) {
				if ("Superbadge".equalsIgnoreCase(m.getModuleType())) {
					if (currentScorePercent >= 50) {
						totalBadges += 3;
					} else {
						totalBadges += 1;
					}
				} else {
					totalBadges += 1;
				}
			}

			// UI Row - container pt module + progres
			VBox row = new VBox(5); // margin vertical
			row.getStyleClass().add("custom-list-view");

			Label lblName = new Label(m.getName() + " (Max: " + m.getNumberOfPoints() + " pts)");
			lblName.setStyle("-fx-font-weight: bold;");

			if ("Superbadge".equalsIgnoreCase(m.getModuleType())) {
				lblName.setText(lblName.getText() + " [SUPERBADGE]");
				lblName.setStyle(lblName.getStyle() + "; -fx-text-fill: #e67e22;");
			}

			TextField txtPoints = new TextField(String.valueOf(currentPoints));
			txtPoints.setPromptText("Points (0-" + m.getNumberOfPoints() + ")");

			// listener validare
			txtPoints.textProperty().addListener((obs, oldV, newV) -> {
				if (!newV.matches("\\d*")) {
					txtPoints.setText(newV.replaceAll("[^\\d]", ""));
				}
				try {
					if (!txtPoints.getText().isEmpty()) {
						int val = Integer.parseInt(txtPoints.getText());
						if (val > m.getNumberOfPoints()) {
							txtPoints.setStyle("-fx-border-color: red;");
						} else {
							txtPoints.setStyle("");
						}
					}
				} catch (NumberFormatException e) {
				}
			});

			scoreInputs.put(m.getModuleId(), txtPoints);

			row.getChildren().addAll(lblName, new Label(m.getDescription()), new Label("Points Obtained:"), txtPoints);
			modulesContainer.getChildren().add(row);
		}

		lblTotalBadges.setText("Total Badges: " + totalBadges);
		lblTotalScore.setText("Total Score for Course: " + totalScore);
	}

	@FXML
	private void handleSaveProgress() {
		if (selectedTrainee == null)
			return;

		List<SelfPacedProgress> toSave = new ArrayList<>();
		boolean hasErrors = false;

		for (Module m : currentModules) {
			TextField tf = scoreInputs.get(m.getModuleId());
			int points = 0;
			try {
				if (!tf.getText().isEmpty())
					points = Integer.parseInt(tf.getText());
			} catch (Exception e) {
			}

			if (points > m.getNumberOfPoints()) {
				hasErrors = true;
				tf.setStyle("-fx-border-color: red;");
			}

			// convertesc puncte -> scor procentual (0-100)
			int scorePercent = 0;
			if (m.getNumberOfPoints() > 0) {
				scorePercent = (int) Math.round(((double) points / m.getNumberOfPoints()) * 100);
			}

			// limitez la 100 preventiv
			if (scorePercent > 100)
				scorePercent = 100;

			SelfPacedProgress p = new SelfPacedProgress();
			p.setEnrolmentId(selectedTrainee.enrolmentId);
			p.setModuleId(m.getModuleId());
			p.setScore(scorePercent);
			p.setNumberOfPoints(m.getNumberOfPoints());
			p.setCompletionDate(java.time.LocalDate.now().toString());

			// Set id daca exista
			if (progressIds.containsKey(m.getModuleId())) {
				p.setProgressId(progressIds.get(m.getModuleId()));
			} else {
				p.setProgressId(0); // daca nu exista, e nou
			}

			toSave.add(p);
		}

		if (hasErrors) { // daca sunt erori, nu salvez
			Alert a = new Alert(Alert.AlertType.ERROR, "Some points exceed max points!");
			a.show();
			return;
		}

		boolean success = progressDAO.saveProgress(toSave);
		if (success) {
			Alert a = new Alert(Alert.AlertType.INFORMATION, "Progress Saved & Badges Updated!");
			a.show();
			// Refresh
			loadModulesAndProgress(comboCourses.getValue().getId(), selectedTrainee.enrolmentId);
		} else {
			Alert a = new Alert(Alert.AlertType.ERROR, "Save Failed.");
			a.show();
		}
	}

	@FXML
	private void handleAddStudent() {
		Course course = comboCourses.getValue();
		if (course == null) {
			new Alert(Alert.AlertType.WARNING, "Please select a course first.").show();
			return;
		}

		// dialog alegere user
		Dialog<User> dialog = new Dialog<>();
		dialog.setTitle("Add Student");
		dialog.setHeaderText("Enroll a student to " + course.getName());

		ButtonType enrollBtn = new ButtonType("Enroll", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(enrollBtn, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		ComboBox<User> userCombo = new ComboBox<>();
		userCombo.setPromptText("Select User...");

		// incarc useri care NU sunt deja inscrisi
		// iau toti userii
		List<User> allUsers = userDAO.getAllUsers();
		// iau id-urile participantilor curenti
		// refolosesc courseDAO.getParticipants pt userIds
		List<Integer> currentParticipantIds = courseDAO.getParticipants(course.getId()).stream()
				.map(p -> (Integer) p.get("userId"))
				.collect(Collectors.toList());

		// Filter
		List<User> available = allUsers.stream()
				.filter(u -> !currentParticipantIds.contains(u.getId()))
				.filter(u -> !"Admin".equalsIgnoreCase(u.getRoleType())) // Optional: filter admins?
				.collect(Collectors.toList());

		userCombo.setItems(FXCollections.observableArrayList(available));
		userCombo.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(User item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getFirstName() + " " + item.getLastName() + " (" + item.getEmail() + ")");
			}
		});
		userCombo.setButtonCell(userCombo.getCellFactory().call(null)); // Button rendering

		grid.add(new Label("Student:"), 0, 0);
		grid.add(userCombo, 1, 0);
		dialog.getDialogPane().setContent(grid);

		// elem de stil
		if (UserSession.getInstance().isDarkMode()) {
			dialog.getDialogPane().getStylesheets()
					.add(getClass().getResource("/com/osf/coursemanagement/style.css").toExternalForm());
			dialog.getDialogPane().getStyleClass().add("dark-theme");
		}

		dialog.setResultConverter(btn -> {
			if (btn == enrollBtn)
				return userCombo.getValue();
			return null;
		});

		Optional<User> res = dialog.showAndWait();
		if (res.isPresent()) {
			User u = res.get();
			boolean ok = enrolmentDAO.enrol(u.getId(), course.getId());
			if (ok) {
				loadTrainees(course); // refresh
			} else {
				new Alert(Alert.AlertType.ERROR, "Failed to enrol student.").show();
			}
		}
	}

	@FXML
	private void handleRemoveStudent() {
		TraineeItem selected = listTrainees.getSelectionModel().getSelectedItem();
		if (selected == null) {
			new Alert(Alert.AlertType.WARNING, "Select a student to remove.").show();
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Remove Student");
		alert.setHeaderText("Unenrol " + selected.name + "?");
		alert.setContentText("This will remove the student from the course. Progress may be lost or orphaned.");

		if (UserSession.getInstance().isDarkMode()) {
			alert.getDialogPane().getStylesheets()
					.add(getClass().getResource("/com/osf/coursemanagement/style.css").toExternalForm());
			alert.getDialogPane().getStyleClass().add("dark-theme");
		}

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				boolean ok = enrolmentDAO.unenrol(selected.enrolmentId);
				if (ok) {
					loadTrainees(comboCourses.getValue());
					hideDetails();
				} else {
					new Alert(Alert.AlertType.ERROR, "Failed to unenrol.").show();
				}
			}
		});
	}

	@FXML
	private void handleBack() {
		try {
			Stage stage = (Stage) rootPane.getScene().getWindow();
			// load dashboard
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
					getClass().getResource("/com/osf/coursemanagement/dashboard-view.fxml"));
			javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), stage.getScene().getWidth(),
					stage.getScene().getHeight());

			// restore dashboard state
			DashboardController c = loader.getController();
			c.restoreState();

			stage.setScene(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleThemeToggle() {
		boolean newMode = !UserSession.getInstance().isDarkMode();
		UserSession.getInstance().setDarkMode(newMode);
		setTheme(newMode);
	}

	private void setTheme(boolean isDark) {
		if (isDark) {
			if (!rootPane.getStyleClass().contains("dark-theme")) {
				rootPane.getStyleClass().add("dark-theme");
			}
			btnThemeToggle.setText("☀️ Light Mode");
		} else {
			rootPane.getStyleClass().remove("dark-theme");
			btnThemeToggle.setText("🌙 Dark Mode");
		}
	}

	// Helper Class
	private static class TraineeItem {
		String name;
		int enrolmentId;
		int userId;

		public TraineeItem(String name, int enrolmentId, int userId) {
			this.name = name;
			this.enrolmentId = enrolmentId;
			this.userId = userId;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
