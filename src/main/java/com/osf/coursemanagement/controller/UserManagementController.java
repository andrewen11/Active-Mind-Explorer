/**
 * Clasa pentru controller-ul de gestionare a utilizatorilor
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.dao.UserDAO;
import com.osf.coursemanagement.util.UIHelper;
import com.osf.coursemanagement.model.User;
import com.osf.coursemanagement.model.UserSession;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManagementController {

	@FXML
	private ListView<User> usersListView;

	// elemente panou detalii
	@FXML
	private VBox detailPanel;
	@FXML
	private Label lblDetailName;
	@FXML
	private Label lblDetailEmail;
	@FXML
	private Label lblDetailRole;
	@FXML
	private Label lblDetailTrainer;

	@FXML
	private Label lblPlaceholder;

	// butoane (footer)
	@FXML
	private Button btnAddUser;
	@FXML
	private Button btnEditUser;
	@FXML
	private Button btnDeleteUser;
	@FXML
	private Button btnThemeToggle;

	@FXML
	private BorderPane rootPane;
	@FXML
	private SplitPane mainSplitPane;
	@FXML
	private AnchorPane paneList;
	@FXML
	private AnchorPane paneDetails;

	private String currentMode = "MANAGE";

	// dao si date
	private final UserDAO userDAO = new UserDAO();
	private final ObservableList<User> userList = FXCollections.observableArrayList();

	// dynamic table pt stats curs
	private TableView<Map<String, Object>> statsTable;

	@FXML
	public void initialize() {
		// setez listview cu un cellfactory mai curat
		usersListView.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(User user, boolean empty) {
				super.updateItem(user, empty);
				if (empty || user == null) {
					setText(null);
				} else {
					setText(user.getFirstName() + " " + user.getLastName());
				}
			}
		});

		// adaugare listener pt schimbari selectie
		usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				showUserDetails(newSelection);
			} else {
				clearUserDetails();
			}
		});

		loadUsers();
		clearUserDetails();
		setupButtons(); // ma asigur ca butoanele reflecta modul default! (light)
		applyTheme(); // mentinerea temei pt panels si dashboard
	}

	public void initData(String mode) {
		if (mode != null && !mode.isEmpty()) {
			this.currentMode = mode;
		}
		applyTheme();
		setupButtons();
		setupLayout();
	}

	private void setupLayout() {
		if (!mainSplitPane.getItems().contains(paneDetails)) {
			mainSplitPane.getItems().add(paneDetails);
			mainSplitPane.setDividerPositions(0.3);
		}
		AnchorPane.setLeftAnchor(usersListView, 0.0);
		AnchorPane.setRightAnchor(usersListView, 0.0);
	}

	private void setupButtons() {
		// o funct mai veche presupunea sa limitez butoanele pt colaborator / trainer
		// arat butoane daca mod e manage
		boolean isManage = "MANAGE".equals(currentMode);
		btnAddUser.setVisible(isManage);
		btnAddUser.setManaged(isManage);
		btnEditUser.setVisible(isManage);
		btnEditUser.setManaged(isManage);
		btnDeleteUser.setVisible(isManage);
		btnDeleteUser.setManaged(isManage);
	}

	private void applyTheme() {
		javafx.scene.Node node = (rootPane != null) ? rootPane : paneList;
		if (node == null)
			return;

		if (UserSession.getInstance().isDarkMode()) {
			if (!node.getStyleClass().contains("dark-theme")) {
				node.getStyleClass().add("dark-theme");
			}
		} else {
			node.getStyleClass().remove("dark-theme");
		}
	}

	@FXML
	private void handleThemeToggle() {
		boolean newMode = !UserSession.getInstance().isDarkMode(); // aplicare tema (apelare switch)
		UserSession.getInstance().setDarkMode(newMode);
		applyTheme();
	}

	// back to dashboard
	@FXML
	private void handleBack() {
		try {
			double width = usersListView.getScene().getWidth();
			double height = usersListView.getScene().getHeight();

			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/com/osf/coursemanagement/dashboard-view.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), width, height);

			DashboardController controller = fxmlLoader.getController();
			controller.restoreState();

			Stage stage = (Stage) usersListView.getScene().getWindow();
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleAddUser() {
		var dialog = showUserDialog(null);
		dialog.showAndWait().ifPresent(u -> {
			loadUsers();
		});
	}

	@FXML
	private void handleEditUser() {
		User selected = usersListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("No selection made.", "First, please select a user to edit!"); // mesaj eroare in alert dialog
																						// window
			return;
		}

		var dialog = showUserDialog(selected); // deschidere dialog pt editare
		dialog.showAndWait().ifPresent(u -> {
			loadUsers();
		});
	}

	@FXML
	private void handleDeleteUser() {
		User selected = usersListView.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("No selection made.", "First, please select a user to delete!");
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // similar cu alerta de confirmare pt stergere
		alert.setTitle("Delete User");
		alert.setHeaderText("Delete " + selected.getFirstName() + " " + selected.getLastName() + "?");
		alert.setContentText("Are you sure? This action CANNOT be undone!");

		UIHelper.styleDialog(alert.getDialogPane());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			if (userDAO.deleteUser(selected.getId())) {
				loadUsers();
			} else {
				showAlert("Error", "Could not delete user.");
			}
		}
	}

	private void showUserDetails(User user) {
		lblPlaceholder.setVisible(false);
		detailPanel.setVisible(true);

		lblDetailName.setText(user.getFirstName() + " " + user.getLastName());
		lblDetailEmail.setText(user.getEmail());
		lblDetailRole.setText(user.getRoleType());

		if ("Trainee".equals(user.getRoleType()) && user.getTrainerId() != null) { // daca e trainee si are trainer id
																					// atunci cauta trainerul in lista
			User trainer = userList.stream().filter(u -> u.getId() == user.getTrainerId()).findFirst().orElse(null);
			lblDetailTrainer.setText(trainer != null ? trainer.getFirstName() + " " + trainer.getLastName()
					: "ID: " + user.getTrainerId());
		} else {
			lblDetailTrainer.setText("-");
		}

		// tabel stats curs
		if (statsTable == null) {
			initStatsTable();
		}

		List<Map<String, Object>> courseStats = userDAO.getUserCourseStats(user.getId());
		if (statsTable != null) {
			statsTable.setItems(FXCollections.observableArrayList(courseStats));
			statsTable.refresh();
		}

		detailPanel.requestLayout();
	}

	private void initStatsTable() {
		statsTable = new TableView<>();
		statsTable.setPrefHeight(200);
		statsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

		TableColumn<Map<String, Object>, String> colCourse = new TableColumn<>("Course");
		colCourse.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("courseName")));
		TableColumn<Map<String, Object>, Integer> colBadges = new TableColumn<>("Badges");
		colBadges.setCellValueFactory(
				data -> new SimpleIntegerProperty((Integer) data.getValue().get("badges")).asObject());
		TableColumn<Map<String, Object>, String> colProgress = new TableColumn<>("Progress");
		colProgress.setCellValueFactory(
				data -> new SimpleStringProperty(String.format("%.1f", (Double) data.getValue().get("progressScore"))));
		TableColumn<Map<String, Object>, String> colLive = new TableColumn<>("Live");
		colLive.setCellValueFactory(
				data -> new SimpleStringProperty(String.format("%.1f", (Double) data.getValue().get("liveScore"))));
		TableColumn<Map<String, Object>, String> colTotal = new TableColumn<>("Total");
		colTotal.setCellValueFactory(
				data -> new SimpleStringProperty(String.format("%.1f", (Double) data.getValue().get("totalScore"))));

		statsTable.getColumns().addAll(colCourse, colBadges, colProgress, colLive, colTotal); // adauga coloanele in
																								// tabel

		detailPanel.setFillWidth(true); // pt set latime panel la 100

		Label lblHeader = new Label("Course Performance:");
		lblHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 0 5 0;");
		detailPanel.getChildren().add(lblHeader);

		detailPanel.getChildren().add(statsTable);
		VBox.setVgrow(statsTable, Priority.ALWAYS); // col care creste in funct de latimea containerului
		statsTable.setMaxHeight(Double.MAX_VALUE);
		statsTable.setMinHeight(150);
	}

	private void clearUserDetails() {
		lblPlaceholder.setVisible(true);
		detailPanel.setVisible(false);
		if (statsTable != null) {
			statsTable.getItems().clear();
		}
	}

	private void loadUsers() {
		userList.setAll(userDAO.getAllUsers()); // adauga useri in lista (data de tip ObservableList)
		usersListView.setItems(userList); // set list drept model ListView
	}

	private Dialog<User> showUserDialog(User user) {
		Dialog<User> dialog = new Dialog<>();
		dialog.setTitle(user == null ? "Add User" : "Edit User");
		dialog.setHeaderText(user == null ? "Create a new user" : "Edit details for " + user.getFirstName());

		UIHelper.styleDialog(dialog.getDialogPane());

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);

		GridPane grid = new GridPane(); // adaug grid in dialog (mimez un tabel)
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField firstName = new TextField();
		firstName.setPromptText("First Name");
		TextField lastName = new TextField();
		lastName.setPromptText("Last Name");
		TextField email = new TextField();
		email.setPromptText("Email");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		ComboBox<String> role = new ComboBox<>();
		role.getItems().addAll("Admin", "Trainer", "Collaborator", "Trainee");
		role.setValue("Trainee");

		Label lblTrainer = new Label("Assign Trainer:");
		ComboBox<User> cmbTrainer = new ComboBox<>();
		cmbTrainer.setPromptText("Select a Trainer");

		// preiau trainerii
		var trainers = userDAO.getAllUsers().stream().filter(u -> "Trainer".equalsIgnoreCase(u.getRoleType()))
				.collect(Collectors.toList());
		cmbTrainer.getItems().addAll(trainers);

		cmbTrainer.setConverter(new javafx.util.StringConverter<>() {
			@Override
			public String toString(User u) {
				return u != null ? u.getFirstName() + " " + u.getLastName() : "";
			}

			@Override
			public User fromString(String string) {
				return null;
			}
		});

		lblTrainer.setVisible(false);
		lblTrainer.setManaged(false);
		cmbTrainer.setVisible(false);
		cmbTrainer.setManaged(false);

		role.valueProperty().addListener((obs, oldVal, newVal) -> {
			boolean isTrainee = "Trainee".equalsIgnoreCase(newVal);
			lblTrainer.setVisible(isTrainee);
			lblTrainer.setManaged(isTrainee);
			cmbTrainer.setVisible(isTrainee);
			cmbTrainer.setManaged(isTrainee);
		});

		if (user != null) {
			firstName.setText(user.getFirstName());
			lastName.setText(user.getLastName());
			email.setText(user.getEmail());
			password.setText(user.getPassword());
			role.setValue(user.getRoleType());

			if (user.getTrainerId() != null) {
				trainers.stream().filter(t -> t.getId() == user.getTrainerId()).findFirst()
						.ifPresent(cmbTrainer::setValue);
			}
			saveButton.setDisable(false);
		}

		if (user != null) {
			boolean isTrainee = "Trainee".equalsIgnoreCase(user.getRoleType());
			lblTrainer.setVisible(isTrainee);
			lblTrainer.setManaged(isTrainee);
			cmbTrainer.setVisible(isTrainee);
			cmbTrainer.setManaged(isTrainee);
		} else {
			boolean isTrainee = "Trainee".equalsIgnoreCase(role.getValue());
			lblTrainer.setVisible(isTrainee);
			lblTrainer.setManaged(isTrainee);
			cmbTrainer.setVisible(isTrainee);
			cmbTrainer.setManaged(isTrainee);
		}

		Label lblWarning = new Label();
		lblWarning.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // stil pt text (atentionare completare
																			// incorecta!)
		lblWarning.setWrapText(true);
		lblWarning.setMaxWidth(300);

		grid.add(new Label("First Name:"), 0, 0);
		grid.add(firstName, 1, 0);
		grid.add(new Label("Last Name:"), 0, 1);
		grid.add(lastName, 1, 1);
		grid.add(new Label("Email:"), 0, 2);
		grid.add(email, 1, 2);
		grid.add(new Label("Password:"), 0, 3);
		grid.add(password, 1, 3);
		grid.add(new Label("Role:"), 0, 4);
		grid.add(role, 1, 4);
		grid.add(lblTrainer, 0, 5);
		grid.add(cmbTrainer, 1, 5);
		grid.add(lblWarning, 0, 6, 2, 1);

		dialog.getDialogPane().setContent(grid);

		// am scos validarile locale (frontend) si le-am mutat in backend (server) -
		// vezi userDAO | saveButton ramane activ mereu

		final Button btSave = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
		btSave.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
			// constr obiect user
			User u = (user != null) ? user : new User();
			u.setFirstName(firstName.getText());
			u.setLastName(lastName.getText());
			u.setEmail(email.getText());
			u.setPassword(password.getText());
			u.setRoleType(role.getValue());
			u.setTrainerId((cmbTrainer.isVisible() && cmbTrainer.getValue() != null) ? cmbTrainer.getValue().getId()
					: null);

			// mecanism salvare (creare sau update)
			try {
				if (user == null) {
					userDAO.createUser(u);
				} else {
					userDAO.updateUser(u);
				}
				// succes! las dialog window sa se inchida
				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == saveButtonType) {
						return u;
					}
					return null;
				});
			} catch (RuntimeException e) {
				// prind eroarea de validare pt backend
				lblWarning.setText(e.getMessage());
				event.consume(); // previn inchiderea dialog
			}
		});
		dialog.setResultConverter(dialogButton -> null);

		return dialog;
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		UIHelper.styleDialog(alert.getDialogPane());
		alert.showAndWait();
	}
}