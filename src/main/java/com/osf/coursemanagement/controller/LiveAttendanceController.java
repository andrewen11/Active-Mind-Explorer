/**
 * Clasa pentru controller-ul prezentei live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.controller;

import com.osf.coursemanagement.dao.CourseDAO;
import com.osf.coursemanagement.dao.LiveAttendanceDAO;
import com.osf.coursemanagement.dao.LiveSessionDAO;
import com.osf.coursemanagement.dao.UserDAO;
import com.osf.coursemanagement.model.Course;
import com.osf.coursemanagement.model.UserSession;
import com.osf.coursemanagement.model.LiveSession;
import com.osf.coursemanagement.model.User;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LiveAttendanceController {

	@FXML
	private BorderPane rootPane; // pus pt stilizare
	@FXML
	private ComboBox<Course> comboCourses;
	@FXML
	private ListView<LiveSession> listSessions;
	@FXML
	private VBox detailsContainer;
	@FXML
	private Label lblPlaceholder;

	@FXML
	private Label lblSessionTheme;
	@FXML
	private Label lblCourseName;
	@FXML
	private Label lblDate;
	@FXML
	private Label lblTrainer;
	@FXML
	private Label lblParticipants;
	@FXML
	private Label lblDescription;
	@FXML
	private Button btnAddSession;
	@FXML
	private Button btnUpdateSession;
	@FXML
	private Button btnRemoveSession;

	private final CourseDAO courseDAO = new CourseDAO();
	private final LiveSessionDAO liveSessionDAO = new LiveSessionDAO();
	private final LiveAttendanceDAO liveAttendanceDAO = new LiveAttendanceDAO();
	private final UserDAO userDAO = new UserDAO();

	private Runnable backHandler;

	public void setOnBack(Runnable handler) {
		this.backHandler = handler;
	}

	public void initialize() {
		// intarziere aplicare tema - astept sa fie scena setata PRIMORDIAL
		rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				applyTheme();
			}
		});

		applyTheme(); // incerc aplicare imediata preventiv
		loadCourses();
		setupListeners();
		this.detailsContainer.setVisible(false);
		this.lblPlaceholder.setVisible(true);
	}

	private void loadCourses() {
		List<Course> courses = courseDAO.getAllCourses();
		ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
		comboCourses.setItems(courseList);

		Callback<ListView<Course>, ListCell<Course>> cellFactory = lv -> new ListCell<>() { // cell factory pt combo
			@Override
			protected void updateItem(Course item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboCourses.setButtonCell(cellFactory.call(null));
		comboCourses.setCellFactory(cellFactory);
	}

	private void setupListeners() {
		comboCourses.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				loadSessions(newVal.getId());
				detailsContainer.setVisible(false);
				lblPlaceholder.setVisible(true);
			}
		});

		listSessions.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				showSessionDetails(newVal);
			}
		});

		listSessions.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(LiveSession item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getDate() + " - " + item.getSessionTheme());
			}
		});
	}

	private void loadSessions(int courseId) {
		List<LiveSession> sessions = liveSessionDAO.getSessionsByCourseId(courseId);
		ObservableList<LiveSession> sessionList = FXCollections.observableArrayList(sessions);
		listSessions.setItems(sessionList);
	}

	private void showSessionDetails(LiveSession session) {
		this.lblPlaceholder.setVisible(false);
		this.detailsContainer.setVisible(true);

		lblSessionTheme.setText(session.getSessionTheme());
		Course selectedCourse = comboCourses.getSelectionModel().getSelectedItem();

		// daca incarc sesiune independent, trb sa gasesc nume curs
		if (selectedCourse == null) {
			// caut in combo
			selectedCourse = comboCourses.getItems().stream().filter(c -> c.getId() == session.getCourseId())
					.findFirst().orElse(null);
		}

		lblCourseName.setText(selectedCourse != null ? selectedCourse.getName() : "Unknown Course");

		if (session.getParsedDate() != null) {
			lblDate.setText(session.getParsedDate().format(DateTimeFormatter.ISO_DATE));
		} else {
			lblDate.setText(session.getDate() != null ? session.getDate() : "N/A");
		}

		// preiau nume trainer prin id
		String trainerText = "Trainer ID: " + session.getTrainerId();
		if (session.getTrainerId() > 0) {
			try {
				User trainer = userDAO.getUserById(session.getTrainerId());
				if (trainer != null) {
					trainerText = "Trainer: " + trainer.getFirstName() + " " + trainer.getLastName();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		lblTrainer.setText(trainerText);

		lblParticipants.setText(String.valueOf(session.getNumberOfParticipants()));
		lblDescription.setText(session.getDescription());
	}

	// --- handlere crud ---

	@FXML
	private void handleAddSession(ActionEvent event) {
		Course selectedCourse = comboCourses.getSelectionModel().getSelectedItem();
		// permit deschidere dialog chiar daca curs e null (creare independenta)
		showSessionDialog(null, selectedCourse);
	}

	@FXML
	private void handleViewParticipants(ActionEvent event) {
		handleUpdateSession(event);
	}

	@FXML
	private void handleUpdateSession(ActionEvent event) {
		LiveSession selectedSession = listSessions.getSelectionModel().getSelectedItem();
		if (selectedSession == null) {
			showAlert(Alert.AlertType.WARNING, "Warning", "Please select a session to update.");
			return;
		}
		Course selectedCourse = comboCourses.getSelectionModel().getSelectedItem();
		// daca e null caut
		if (selectedCourse == null) {
			selectedCourse = comboCourses.getItems().stream()
					.filter(c -> c.getId() == selectedSession.getCourseId())
					.findFirst().orElse(null);
		}

		showSessionDialog(selectedSession, selectedCourse);
	}

	@FXML
	private void handleRemoveSession(ActionEvent event) {
		LiveSession selectedSession = listSessions.getSelectionModel().getSelectedItem();
		if (selectedSession == null) {
			showAlert(Alert.AlertType.WARNING, "Warning", "Please select a session to remove.");
			return;
		}

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Session");
		alert.setHeaderText("Delete " + selectedSession.getSessionTheme() + "?");
		alert.setContentText("Are you sure you want to delete this session?");
		styleDialog(alert.getDialogPane());

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				liveSessionDAO.deleteSession(selectedSession.getSessionId());
				loadSessions(selectedSession.getCourseId());
				detailsContainer.setVisible(false);
				lblPlaceholder.setVisible(true);
			}
		});
	}

	private void showSessionDialog(LiveSession existingSession, Course preSelectedCourse) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(existingSession == null ? "Add Session" : "Edit Session");
		dialog.setHeaderText(existingSession == null ? "Create New Session" : "Edit Session Details");
		styleDialog(dialog.getDialogPane());

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		// layout
		VBox mainContainer = new VBox(10);
		mainContainer.setPadding(new Insets(10));

		// form de sus
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		// selectie curs (doar daca adaug nou si nu pre-selectat)
		ComboBox<Course> dlgComboCourse = new ComboBox<>();
		if (existingSession == null && preSelectedCourse == null) {
			dlgComboCourse.setItems(comboCourses.getItems()); // Reuse list
			dlgComboCourse.setPromptText("Select Course");
			// copiez logica cell factory
			dlgComboCourse.setCellFactory(comboCourses.getCellFactory());
			dlgComboCourse.setButtonCell(comboCourses.getButtonCell());

			grid.add(new Label("Course:"), 0, 0);
			grid.add(dlgComboCourse, 1, 0);
		} else {
			// arat doar label
			grid.add(new Label("Course:"), 0, 0);
			String cName = preSelectedCourse != null ? preSelectedCourse.getName()
					: (existingSession != null ? "ID: " + existingSession.getCourseId() : "N/A");
			grid.add(new Label(cName), 1, 0);
		}

		TextField themeField = new TextField();
		themeField.setPromptText("Session Theme");
		DatePicker datePicker = new DatePicker();

		TextArea descArea = new TextArea();
		descArea.setPrefRowCount(2);

		grid.add(new Label("Theme:"), 0, 1);
		grid.add(themeField, 1, 1);
		grid.add(new Label("Date:"), 0, 2);
		grid.add(datePicker, 1, 2);
		grid.add(new Label("Description:"), 0, 3);
		grid.add(descArea, 1, 3);

		// selectie trainer
		ComboBox<User> comboTrainer = new ComboBox<>();
		comboTrainer.setPromptText("Select Trainer");
		List<User> allUsers = userDAO.getAllUsers();
		List<User> trainers = allUsers.stream()
				.filter(u -> "Trainer".equalsIgnoreCase(u.getRoleType()))
				.collect(Collectors.toList());
		comboTrainer.setItems(FXCollections.observableArrayList(trainers));

		grid.add(new Label("Trainer:"), 0, 4);
		grid.add(comboTrainer, 1, 4);

		// tabel participanti - vezi inspre final despre participantRow
		TableView<ParticipantRow> tableParticipants = new TableView<>();
		tableParticipants.setEditable(true);
		tableParticipants.setPrefHeight(200);

		TableColumn<ParticipantRow, Boolean> colSelect = new TableColumn<>("Present");
		colSelect.setCellValueFactory(cell -> cell.getValue().selectedProperty());
		colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
		colSelect.setEditable(true);

		TableColumn<ParticipantRow, String> colName = new TableColumn<>("Student");
		colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
		colName.setEditable(false);

		TableColumn<ParticipantRow, Integer> colScore = new TableColumn<>("Score (0-100)");
		colScore.setCellValueFactory(cell -> cell.getValue().scoreProperty().asObject());
		// cell custom pt editare scor? sau simplu int?
		// permit editare simpla text momentan
		colScore.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colScore.setOnEditCommit(event -> {
			if (event.getNewValue() != null) {
				event.getRowValue().setScore(event.getNewValue());
			}
		});
		colScore.setEditable(true);

		TableColumn<ParticipantRow, String> colNotes = new TableColumn<>("Notes");
		colNotes.setCellValueFactory(cell -> cell.getValue().notesProperty());
		colNotes.setCellFactory(TextFieldTableCell.forTableColumn());
		colNotes.setOnEditCommit(event -> {
			event.getRowValue().setNotes(event.getNewValue());
		});
		colNotes.setEditable(true);

		tableParticipants.getColumns().addAll(colSelect, colName, colScore, colNotes);

		mainContainer.getChildren().addAll(grid, new Label("Participants (Grading):"), tableParticipants);
		dialog.getDialogPane().setContent(mainContainer);

		// logica incarcare date
		if (existingSession != null) {
			themeField.setText(existingSession.getSessionTheme()); // setare tema
			if (existingSession.getParsedDate() != null)
				datePicker.setValue(existingSession.getParsedDate());
			descArea.setText(existingSession.getDescription());

			if (existingSession.getTrainerId() > 0) {
				comboTrainer.getItems().stream()
						.filter(u -> u.getId() == existingSession.getTrainerId())
						.findFirst()
						.ifPresent(comboTrainer.getSelectionModel()::select);
			}

			// incarc participanti + prezenta existenta
			loadParticipantsForTable(tableParticipants, existingSession.getCourseId(), existingSession.getSessionId());
		} else {
			datePicker.setValue(LocalDate.now());

			if (preSelectedCourse != null) {
				loadParticipantsForTable(tableParticipants, preSelectedCourse.getId(), -1);
			}

			// listener selectie curs in dialog
			dlgComboCourse.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
				if (newV != null) {
					loadParticipantsForTable(tableParticipants, newV.getId(), -1);
				}
			});
		}

		// convertor rezultat
		dialog.setResultConverter(buttonType -> {
			if (buttonType == saveButtonType) {
				return buttonType;
			}
			return null;
		});

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == saveButtonType) {
			// salvare sesiune
			LiveSession s = existingSession != null ? existingSession : new LiveSession();

			if (existingSession == null) {
				if (preSelectedCourse != null)
					s.setCourseId(preSelectedCourse.getId());
				else if (dlgComboCourse.getValue() != null)
					s.setCourseId(dlgComboCourse.getValue().getId());
				else {
					showAlert(Alert.AlertType.ERROR, "Error", "No course selected!");
					return;
				}
			}

			s.setSessionTheme(themeField.getText());
			if (datePicker.getValue() != null)
				s.setDate(datePicker.getValue().toString());
			s.setDescription(descArea.getText());
			if (comboTrainer.getValue() != null) {
				s.setTrainerId(comboTrainer.getValue().getId());
			}

			// calculez nr participanti
			long count = tableParticipants.getItems().stream().filter(ParticipantRow::isSelected).count();
			s.setNumberOfParticipants((int) count);

			LiveSession savedSession;
			if (existingSession == null) {
				savedSession = liveSessionDAO.createSession(s);
			} else {
				savedSession = liveSessionDAO.updateSession(s);
			}

			// salvez prezenta daca sesiune salvata cu succes
			if (savedSession != null) {
				List<Map<String, Object>> attendanceList = new ArrayList<>();
				for (ParticipantRow row : tableParticipants.getItems()) {
					if (row.isSelected()) {
						Map<String, Object> map = new HashMap<>();
						map.put("enrolmentId", row.getEnrolmentId());
						map.put("score", row.getScore());
						map.put("notes", row.getNotes());
						attendanceList.add(map);

						// debug
						// System.out.println("Saving: " + row.getName() + " Score: " + row.getScore());
					}
				}

				boolean ok = liveAttendanceDAO.saveAttendance(savedSession.getSessionId(), attendanceList);
				if (!ok) {
					showAlert(Alert.AlertType.ERROR, "Error", "Session saved but Attendance save failed.");
				}

				// refresh view
				int cid = savedSession.getCourseId();
				loadSessions(cid);
				// daca am dat update la sesiune curenta-> refresh detalii
				if (existingSession != null && listSessions.getSelectionModel().getSelectedItem() == existingSession) {
					showSessionDetails(savedSession);
				}
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to save session.");
			}
		}
	}

	private void loadParticipantsForTable(TableView<ParticipantRow> table, int courseId, int sessionId) {
		// preiau utilizatori inscrisi
		List<Map<String, Object>> participants = courseDAO.getParticipants(courseId);

		// preiau prezenta existenta (daca editez)
		Map<Integer, Map<String, Object>> existingAttendance = new HashMap<>();
		if (sessionId > 0) {
			List<Map<String, Object>> att = liveAttendanceDAO.getAttendanceBySessionId(sessionId);
		}

		ObservableList<ParticipantRow> rows = FXCollections.observableArrayList(); // aici sunt participantii care sunt
																					// inscrisi la curs!
		for (Map<String, Object> p : participants) {
			int eid = (int) p.get("enrolmentId");
			String name = (String) p.get("studentName");

			ParticipantRow row = new ParticipantRow(eid, 0, name, false, 0, "");
			rows.add(row);
		}

		// daca sunt in edit mode, incarc prezenta existenta
		if (sessionId > 0) {
			List<Map<String, Object>> att = liveAttendanceDAO.getAttendanceBySessionId(sessionId); // prezentele
																									// existente
			for (Map<String, Object> a : att) {
				String sName = (String) a.get("studentName");
				// match dupa nume
				for (ParticipantRow r : rows) {
					if (r.getName().equals(sName)) {
						r.setSelected(true);
						r.setScore((int) a.get("score"));
						r.setNotes((String) a.get("notes"));
					}
				}
			}
		}

		table.setItems(rows);
	}

	// clasa auxiliara pentru a putea afisa participantii in tabel ulterior (in
	// dialogul de editare)
	public static class ParticipantRow {
		private final IntegerProperty enrolmentId;
		private final IntegerProperty userId;
		public final StringProperty name;
		private final BooleanProperty selected;
		private final IntegerProperty score;
		private final StringProperty notes;

		public ParticipantRow(int enrolmentId, int userId, String name, boolean selected, int score, String notes) {
			this.enrolmentId = new SimpleIntegerProperty(enrolmentId);
			this.userId = new SimpleIntegerProperty(userId);
			this.name = new SimpleStringProperty(name);
			this.selected = new SimpleBooleanProperty(selected);
			this.score = new SimpleIntegerProperty(score);
			this.notes = new SimpleStringProperty(notes);
		}

		public int getEnrolmentId() {
			return enrolmentId.get();
		}

		public String getName() {
			return name.get();
		}

		public StringProperty nameProperty() {
			return name;
		}

		public boolean isSelected() {
			return selected.get();
		}

		public void setSelected(boolean v) {
			selected.set(v);
		}

		public BooleanProperty selectedProperty() {
			return selected;
		}

		public int getScore() {
			return score.get();
		}

		public void setScore(int v) {
			score.set(v);
		}

		public IntegerProperty scoreProperty() {
			return score;
		}

		public String getNotes() {
			return notes.get();
		}

		public void setNotes(String v) {
			notes.set(v);
		}

		public StringProperty notesProperty() {
			return notes;
		}
	}

	@FXML
	private void handleBack(ActionEvent event) {
		if (backHandler != null) {
			backHandler.run();
		} else {
			try {
				double width = rootPane.getScene().getWidth();
				double height = rootPane.getScene().getHeight();
				FXMLLoader fxmlLoader = new FXMLLoader(
						getClass().getResource("/com/osf/coursemanagement/dashboard-view.fxml"));
				Scene scene = new Scene(fxmlLoader.load(), width, height);
				Stage stage = (Stage) rootPane.getScene().getWindow();
				stage.setScene(scene);
			} catch (IOException e) {
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Dashboard.");
			}
		}
	}

	@FXML
	private void handleThemeToggle(ActionEvent event) {
		boolean newMode = !UserSession.getInstance().isDarkMode();
		UserSession.getInstance().setDarkMode(newMode);
		applyTheme();
	}

	private void applyTheme() {
		if (rootPane == null || rootPane.getScene() == null)
			return;
		boolean isDark = UserSession.getInstance().isDarkMode();
		Scene scene = rootPane.getScene();
		scene.getStylesheets().clear();
		scene.getStylesheets().add(getClass().getResource("/com/osf/coursemanagement/style.css").toExternalForm());
		if (isDark) {
			if (!rootPane.getStyleClass().contains("dark-theme"))
				rootPane.getStyleClass().add("dark-theme");
		} else {
			rootPane.getStyleClass().remove("dark-theme");
		}
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		styleDialog(alert.getDialogPane());
		alert.showAndWait();
	}

	private void styleDialog(DialogPane pane) {
		if (UserSession.getInstance().isDarkMode()) {
			pane.getStyleClass().add("dark-theme");
			try {
				pane.getStylesheets()
						.add(getClass().getResource("/com/osf/coursemanagement/style.css").toExternalForm());
			} catch (Exception e) {
			}
		}
	}
}
