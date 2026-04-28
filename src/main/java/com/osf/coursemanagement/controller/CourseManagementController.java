/**
 * Clasa pentru controller-ul de gestionare a cursurilor
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.dao.CourseDAO;
import com.osf.coursemanagement.util.UIHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.Course;
import com.osf.coursemanagement.model.Module;
import com.osf.coursemanagement.model.UserSession;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CourseManagementController implements Initializable {

	@FXML
	private javafx.scene.layout.BorderPane rootPane;

	@FXML
	private SplitPane mainSplitPane;
	@FXML
	private AnchorPane paneList;

	private void applyTheme() {
		// folosesc rootPane direct daca exista, altfel fallback pt siguranta
		javafx.scene.Node node = (rootPane != null) ? rootPane : paneList;
		if (node == null)
			return;

		// daca rootPane e injectat, aplic direct pe el sau pe continutul scenei
		// daca folosesc rootpane, il <<customizez>> direct pe el
		// logica veche era root.getStyleClass()... adica de obicei aplicat nodului
		// radacina

		if (UserSession.getInstance().isDarkMode()) {
			if (!node.getStyleClass().contains("dark-theme")) {
				node.getStyleClass().add("dark-theme");
			}
		} else {
			node.getStyleClass().remove("dark-theme");
		}
	}

	@FXML
	private AnchorPane paneDetails;
	@FXML
	private ListView<Course> coursesListView;
	@FXML
	private Label lblDetailModules;

	@FXML
	private VBox detailPanel;
	@FXML
	private Label lblPlaceholder;
	@FXML
	private Label lblDetailName;
	@FXML
	private Label lblDetailDescription;
	@FXML
	private Label lblDetailDuration;
	@FXML
	private Label lblDetailEnrolments;

	@FXML
	private Button btnAddCourse;
	@FXML
	private Button btnEditCourse;

	@FXML
	private Button btnDeleteCourse;
	@FXML
	private Button btnThemeToggle;

	private final CourseDAO courseDAO = new CourseDAO();
	private Runnable backHandler;
	private String currentMode = "MANAGE";

	// initializeaza controllerul
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupLayout();
		loadCourses();
		setupButtons(); // ma asigur ca butoanele reflecta modul default
		applyTheme();

		// listener pt selectie
		coursesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				showCourseDetails(newVal);
			} else {
				showPlaceholder();
			}
		});

		// dublu click pt editare (triggers editCourse)
		coursesListView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2 && coursesListView.getSelectionModel().getSelectedItem() != null) {
				handleEditCourse(null);
			}
		});
	}

	// init controllerul cu datele de la view
	public void initData(String mode) {
		if (mode != null && !mode.isEmpty()) {
			this.currentMode = mode;
		}
		applyTheme();
		setupButtons();
		setupLayout();
	}

	public void setOnBack(Runnable handler) {
		this.backHandler = handler;
	}

	private void setupLayout() {
		// resetez layout la default mai intai
		if (!mainSplitPane.getItems().contains(paneDetails)) {
			mainSplitPane.getItems().add(paneDetails);
			mainSplitPane.setDividerPositions(0.4);
		}
		AnchorPane.setLeftAnchor(coursesListView, 0.0);
		AnchorPane.setRightAnchor(coursesListView, 0.0);

		showPlaceholder();
	}

	private void setupButtons() {
		// default: arat butoane daca mod e manage
		boolean isManage = "MANAGE".equals(currentMode);
		btnAddCourse.setVisible(isManage);
		btnAddCourse.setManaged(isManage);
		btnEditCourse.setVisible(isManage);
		btnEditCourse.setManaged(isManage);
		btnDeleteCourse.setVisible(isManage);
		btnDeleteCourse.setManaged(isManage);
	}

	private void loadCourses() {
		Task<List<Course>> task = new Task<>() {
			@Override
			protected List<Course> call() throws Exception {
				return courseDAO.getAllCourses();
			}
		};

		task.setOnSucceeded(e -> {
			coursesListView.getItems().setAll(task.getValue());
		});

		task.setOnFailed(e -> {
			e.getSource().getException().printStackTrace();
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load courses.");
				UIHelper.styleDialog(alert.getDialogPane());
				alert.show();
			});
		});

		new Thread(task).start();
	}

	private void showCourseDetails(Course course) {
		lblPlaceholder.setVisible(false);
		detailPanel.setVisible(true);

		lblDetailName.setText(course.getName());
		lblDetailDescription.setText(course.getDescription());
		lblDetailDuration.setText(course.getCourseDuration());

		// preiau detalii complexe (nr inscrieri, module)
		fetchCourseStats(course.getId());
	}

	private void showPlaceholder() {
		lblPlaceholder.setVisible(true);
		detailPanel.setVisible(false);
	}

	private void fetchCourseStats(int courseId) {
		Task<Map<String, Object>> task = new Task<>() { // task (background thread cu partea de backend)
			@Override
			protected Map<String, Object> call() throws Exception {
				return courseDAO.getCourseDetails(courseId);
			}
		};

		task.setOnSucceeded(e -> {
			Map<String, Object> stats = task.getValue();
			if (stats != null) {
				// nr inscrieri (platform.runLater pt thread safety!!!)
				Platform.runLater(() -> {
					if (stats.containsKey("enrolmentCount")) {
						lblDetailEnrolments.setText(String.valueOf(stats.get("enrolmentCount")));
					} else {
						lblDetailEnrolments.setText("0");
					}
				});

				// module
				if (stats.containsKey("modules")) {
					try {
						ObjectMapper mapper = new ObjectMapper(); // mapper pt deserializare (a se vedea si in restul
																	// controllerelor)
						List<Module> modules = mapper.convertValue(stats.get("modules"),
								new TypeReference<List<Module>>() {
								});

						Platform.runLater(() -> {
							if (modules != null && !modules.isEmpty()) {
								String moduleText = modules.stream()
										.map(m -> m.getName() + " (" + m.getNumberOfPoints() + " pts)")
										.collect(java.util.stream.Collectors.joining("; "));
								lblDetailModules.setText(moduleText);
							} else {
								lblDetailModules.setText("No modules.");
							}
						});

					} catch (Exception ex) {
						System.err.println("Error deserializing modules: " + ex.getMessage());
						ex.printStackTrace();
						Platform.runLater(() -> lblDetailModules.setText("Error loading modules."));
					}
				}
			}
		});

		task.setOnFailed(e -> { // daca e esuat= afisez eroare
			System.err.println("Failed to fetch course stats");
			e.getSource().getException().printStackTrace();
		});

		new Thread(task).start();
	}

	@FXML
	private void handleAddCourse(ActionEvent event) {
		Dialog<Course> dialog = showCourseDialog("ADD", null);
		dialog.showAndWait().ifPresent(course -> {
			courseDAO.createCourse(course);
			loadCourses();
		});
	}

	@FXML
	private void handleEditCourse(ActionEvent event) { // editare curs (dialog date)
		Course selected = coursesListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert(Alert.AlertType.WARNING, "Warning", "Please select a course to edit.");
			return;
		}

		Dialog<Course> dialog = showCourseDialog("EDIT", selected);
		dialog.showAndWait().ifPresent(course -> {
			// este id ul preluat pt validarea update ului?
			course.setId(selected.getId());
			courseDAO.updateCourse(course);
			loadCourses();
			showCourseDetails(course); // refresh det
		});
	}

	@FXML
	private void handleDeleteCourse(ActionEvent event) {
		Course selected = coursesListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert(Alert.AlertType.WARNING, "Warning", "Select a course to delete.");
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Course");
		alert.setHeaderText("Delete " + selected.getName() + "?");
		alert.setContentText("Are you sure you want to delete this course?");
		UIHelper.styleDialog(alert.getDialogPane());

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				courseDAO.deleteCourse(selected.getId());
				loadCourses();
				showPlaceholder();
			}
		});
	}

	private Dialog<Course> showCourseDialog(String mode, Course course) {
		Dialog<Course> dialog = new Dialog<>();
		dialog.setTitle(course == null ? "Add Course" : "Edit Course");
		dialog.setHeaderText(course == null ? "Create a new course" : "Edit details for " + course.getName());

		UIHelper.styleDialog(dialog.getDialogPane());

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		// dezactivez buton salvare initial
		javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
		saveButton.setDisable(true);

		GridPane grid = new javafx.scene.layout.GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		TextField nameField = new TextField();
		nameField.setPromptText("Course Name");
		TextArea descriptionArea = new TextArea();
		descriptionArea.setPromptText("Description");
		descriptionArea.setPrefRowCount(3);
		TextField durationField = new TextField();
		durationField.setPromptText("Duration (e.g. 4 weeks)");

		grid.add(new Label("Course Name:"), 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(new Label("Description:"), 0, 1);
		grid.add(descriptionArea, 1, 1);
		grid.add(new Label("Duration:"), 0, 2);
		grid.add(durationField, 1, 2);

		// populez campurile la editare
		if (course != null) {
			nameField.setText(course.getName());
			descriptionArea.setText(course.getDescription());
			durationField.setText(course.getCourseDuration());
			saveButton.setDisable(false);
		}

		// logica pt validare live - xhange listener pt validare/verif campuri
		ChangeListener<String> validator = (obs, oldVal, newVal) -> {
			String name = nameField.getText().trim();
			String desc = descriptionArea.getText().trim();
			String dur = durationField.getText().trim();

			boolean valid = !name.isEmpty() && !desc.isEmpty() && !dur.isEmpty();
			saveButton.setDisable(!valid);
		};

		nameField.textProperty().addListener(validator);
		descriptionArea.textProperty().addListener(validator);
		durationField.textProperty().addListener(validator);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				Course c = (course != null) ? course : new Course();
				c.setName(nameField.getText());
				c.setDescription(descriptionArea.getText());
				c.setCourseDuration(durationField.getText());
				return c;
			}
			return null;
		});

		return dialog;
	}

	// alert dialog
	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		UIHelper.styleDialog(alert.getDialogPane());
		alert.showAndWait();
	}

	@FXML
	private void handleThemeToggle(ActionEvent event) {
		if (paneList.getScene() == null)
			return;

		boolean newMode = !UserSession.getInstance().isDarkMode();
		UserSession.getInstance().setDarkMode(newMode);
		applyTheme();
	}

	@FXML
	private void handleBack(ActionEvent event) {
		try {
			double width = coursesListView.getScene().getWidth();
			double height = coursesListView.getScene().getHeight();

			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/com/osf/coursemanagement/dashboard-view.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), width, height);

			DashboardController controller = fxmlLoader.getController();
			controller.restoreState();

			Stage stage = (Stage) coursesListView.getScene().getWindow();
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// getters/setters extra pt testare
	public VBox getDetailPanel() {
		return detailPanel;
	}

	public void setDetailPanel(VBox detailPanel) {
		this.detailPanel = detailPanel;
	}
}
