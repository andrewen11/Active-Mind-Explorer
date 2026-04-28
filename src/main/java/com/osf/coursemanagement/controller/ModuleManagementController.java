/**
 * Clasa pentru controller-ul gestionarii modulelor
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.util.UIHelper;

import com.osf.coursemanagement.dao.CourseDAO;
import com.osf.coursemanagement.dao.ModuleDAO;
import com.osf.coursemanagement.model.Course;
import com.osf.coursemanagement.model.Module;
import com.osf.coursemanagement.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;

public class ModuleManagementController {

	@FXML
	private ListView<Module> modulesListView;

	// elemente panou detalii
	@FXML
	private VBox detailPanel;
	@FXML
	private Label lblDetailName;
	@FXML
	private Label lblDetailDescription;
	@FXML
	private Label lblDetailType;
	@FXML
	private Label lblDetailCourse;
	@FXML
	private Label lblDetailPoints;
	@FXML
	private Label lblDetailCriteria;
	@FXML
	private Label lblPlaceholder;

	// layout
	@FXML
	private BorderPane rootPane;
	@FXML
	private SplitPane mainSplitPane;
	@FXML
	private AnchorPane paneList;
	@FXML
	private AnchorPane paneDetails;

	// butoane
	@FXML
	private Button btnAdd;
	@FXML
	private Button btnEdit;
	@FXML
	private Button btnDelete;
	@FXML
	private Button btnThemeToggle;

	private final ModuleDAO moduleDAO = new ModuleDAO();
	private final CourseDAO courseDAO = new CourseDAO();
	private final ObservableList<Module> moduleList = FXCollections.observableArrayList();
	private String currentMode = "MANAGE";

	@FXML
	public void initialize() {
		// setup listview
		modulesListView.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(Module item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});

		// listener
		modulesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				showModuleDetails(newVal);
			} else {
				clearModuleDetails();
			}
		});

		loadModules();
		clearModuleDetails();
		setupButtons(); // ma asigur ca butoanele reflecta modul default
		applyTheme();
	}

	public void initData(String mode) {
		if (mode != null && !mode.isEmpty()) {
			this.currentMode = mode;
		}
		applyTheme();
		setupLayout();
		setupButtons();
	}

	private void setupLayout() {
		// reset layout
		if (!mainSplitPane.getItems().contains(paneDetails)) {
			mainSplitPane.getItems().add(paneDetails);
			mainSplitPane.setDividerPositions(0.4);
		}

		// reset ancore lista
		AnchorPane.setLeftAnchor(modulesListView, 0.0);
		AnchorPane.setRightAnchor(modulesListView, 0.0);

	}

	private void setupButtons() {
		boolean isManage = "MANAGE".equals(currentMode);
		btnAdd.setVisible(isManage);
		btnAdd.setManaged(isManage);
		btnEdit.setVisible(isManage);
		btnEdit.setManaged(isManage);
		btnDelete.setVisible(isManage);
		btnDelete.setManaged(isManage);
	}

	private void loadModules() {
		moduleList.setAll(moduleDAO.getAllModules());
		modulesListView.setItems(moduleList);
	}

	private void showModuleDetails(Module module) {
		lblPlaceholder.setVisible(false);
		detailPanel.setVisible(true);

		lblDetailName.setText(module.getName());
		lblDetailDescription.setText(module.getDescription());
		lblDetailType.setText(module.getModuleType());
		lblDetailPoints.setText(String.valueOf(module.getNumberOfPoints()));
		lblDetailCriteria.setText(module.getCompletionCriteria());

		// gasesc nume curs (ineficient dar merge pt afisare)
		Course c = courseDAO.getAllCourses().stream()
				.filter(course -> course.getId() == module.getCourseId())
				.findFirst()
				.orElse(null);
		lblDetailCourse.setText(c != null ? c.getName() : "ID: " + module.getCourseId());
	}

	private void clearModuleDetails() {
		lblPlaceholder.setVisible(true);
		detailPanel.setVisible(false);
		lblDetailName.setText("");
		lblDetailDescription.setText("");
		lblDetailType.setText("");
		lblDetailPoints.setText("");
		lblDetailCriteria.setText("");
		lblDetailCourse.setText("");
	}

	@FXML
	private void handleAdd() {
		var dialog = showModuleDialog("ADD", null);
		dialog.showAndWait().ifPresent(m -> {
			if (moduleDAO.createModule(m)) {
				loadModules();
			} else {
				showAlert("Error", "Could not create module.");
			}
		});
	}

	@FXML
	private void handleEdit() {
		Module selected = modulesListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("No selection", "Please select a module to edit.");
			return;
		}
		var dialog = showModuleDialog("EDIT", selected);
		dialog.showAndWait().ifPresent(m -> {
			if (moduleDAO.updateModule(m)) {
				loadModules();
				showModuleDetails(m); // Refresh details
			} else {
				showAlert("Error", "Could not update module.");
			}
		});
	}

	@FXML
	private void handleDelete() {
		Module selected = modulesListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("No selection", "Please select a module to delete.");
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Module");
		alert.setHeaderText("Delete " + selected.getName() + "?");
		alert.setContentText("Are you sure? This cannot be undone.");
		UIHelper.styleDialog(alert.getDialogPane());

		if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
			if (moduleDAO.deleteModule(selected.getId())) {
				loadModules();
				clearModuleDetails();
			} else {
				showAlert("Error", "Could not delete module.");
			}
		}
	}

	// dialog pt adaugare/editare module
	private Dialog<Module> showModuleDialog(String mode, Module module) {
		Dialog<Module> dialog = new Dialog<>();
		dialog.setTitle(mode.equals("ADD") ? "Add Module" : "Edit Module");
		dialog.setHeaderText(mode.equals("ADD") ? "Create New Module" : "Edit Module Details");
		UIHelper.styleDialog(dialog.getDialogPane());

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ComboBox<Course> cmbCourse = new ComboBox<>();
		cmbCourse.setPromptText("Select Course");
		cmbCourse.getItems().setAll(courseDAO.getAllCourses());
		cmbCourse.setConverter(new StringConverter<>() {
			@Override
			public String toString(Course c) {
				return c != null ? c.getName() : "";
			}

			@Override
			public Course fromString(String s) {
				return null;
			}
		});

		TextField txtName = new TextField();
		txtName.setPromptText("Module Name");

		TextArea txtDesc = new TextArea();
		txtDesc.setPromptText("Description");
		txtDesc.setPrefRowCount(3);

		TextArea txtCriteria = new TextArea();
		txtCriteria.setPromptText("Completion Criteria");
		txtCriteria.setPrefRowCount(2);

		ComboBox<String> cmbType = new ComboBox<>();
		cmbType.getItems().addAll("Lesson", "Project", "Quiz", "Superbadge");
		cmbType.setValue("Lesson");

		TextField txtPoints = new TextField();
		txtPoints.setPromptText("Points (e.g. 100)");

		grid.add(new Label("Course:"), 0, 0);
		grid.add(cmbCourse, 1, 0);
		grid.add(new Label("Name:"), 0, 1);
		grid.add(txtName, 1, 1);
		grid.add(new Label("Description:"), 0, 2);
		grid.add(txtDesc, 1, 2);
		grid.add(new Label("Criteria:"), 0, 3);
		grid.add(txtCriteria, 1, 3);
		grid.add(new Label("Type:"), 0, 4);
		grid.add(cmbType, 1, 4);
		grid.add(new Label("Points:"), 0, 5);
		grid.add(txtPoints, 1, 5);

		dialog.getDialogPane().setContent(grid);

		// pre-completez daca editez
		if (module != null) {
			txtName.setText(module.getName());
			txtDesc.setText(module.getDescription());
			txtCriteria.setText(module.getCompletionCriteria());
			cmbType.setValue(module.getModuleType());
			txtPoints.setText(String.valueOf(module.getNumberOfPoints()));

			// gasesc curs dupa id
			for (Course c : cmbCourse.getItems()) {
				if (c.getId() == module.getCourseId()) {
					cmbCourse.setValue(c);
					break;
				}
			}
		}

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				try {
					Module m = (module != null) ? module : new Module();
					m.setName(txtName.getText());
					m.setDescription(txtDesc.getText());
					m.setCompletionCriteria(txtCriteria.getText());
					m.setModuleType(cmbType.getValue());
					m.setNumberOfPoints(Integer.parseInt(txtPoints.getText()));
					if (cmbCourse.getValue() != null) {
						m.setCourseId(cmbCourse.getValue().getId());
					}
					return m;
				} catch (NumberFormatException e) {
					showAlert("Invalid Input", "Points must be a number.");
					return null;
				}
			}
			return null;
		});

		return dialog;
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(content);
		UIHelper.styleDialog(alert.getDialogPane());
		alert.showAndWait();
	}

	@FXML
	private void handleBack() {
		if (onBack != null) {
			onBack.run();
		}
	}

	private Runnable onBack;

	public void setOnBack(Runnable onBack) {
		this.onBack = onBack;
	}

	@FXML
	private void handleThemeToggle() {
		boolean newMode = !UserSession.getInstance().isDarkMode();
		UserSession.getInstance().setDarkMode(newMode);
		applyTheme();
	}

	private void applyTheme() {
		if (rootPane == null)
			return;
		if (UserSession.getInstance().isDarkMode()) {
			if (!rootPane.getStyleClass().contains("dark-theme")) {
				rootPane.getStyleClass().add("dark-theme");
			}
			btnThemeToggle.setText("Light Mode");
		} else {
			rootPane.getStyleClass().remove("dark-theme");
			btnThemeToggle.setText("Dark Mode");
		}
	}
}
