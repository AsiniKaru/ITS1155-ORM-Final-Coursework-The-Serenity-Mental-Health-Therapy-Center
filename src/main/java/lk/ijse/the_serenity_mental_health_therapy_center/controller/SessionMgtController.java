package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SessionMgtController {

    @FXML private TextField txtSearch;
    @FXML private TableView<TherapySessionDTO> tblSessions;
    @FXML private TableColumn<TherapySessionDTO, String> colId;
    @FXML private TableColumn<TherapySessionDTO, String> colPatient;
    @FXML private TableColumn<TherapySessionDTO, String> colTherapist;
    @FXML private TableColumn<TherapySessionDTO, String> colProgram;
    @FXML private TableColumn<TherapySessionDTO, String> colSchedule;
    @FXML private TableColumn<TherapySessionDTO, String> colUpfront;
    @FXML private TableColumn<TherapySessionDTO, String> colStatus;

    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    private final ObservableList<TherapySessionDTO> sessionList = FXCollections.observableArrayList();

    private final Map<String, String> patientMap = new HashMap<>();
    private final Map<String, String> therapistMap = new HashMap<>();
    private final Map<String, String> programMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Bind columns
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSessionId()));
        colPatient.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getPatientId();
            return new SimpleStringProperty(patientMap.getOrDefault(id, "Patient ID: " + id));
        });
        colTherapist.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getTherapistId();
            return new SimpleStringProperty(therapistMap.getOrDefault(id, "Therapist ID: " + id));
        });
        colProgram.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getProgramId();
            return new SimpleStringProperty(programMap.getOrDefault(id, "Program ID: " + id));
        });
        colSchedule.setCellValueFactory(cellData -> {
            TherapySessionDTO dto = cellData.getValue();
            String date = dto.getSessionDate() != null ? dto.getSessionDate().toString() : "";
            String time = dto.getSessionTime() != null ? dto.getSessionTime().toString() : "";
            return new SimpleStringProperty(date + " at " + time);
        });
        colUpfront.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getUpfrontPayment() != null ? cellData.getValue().getUpfrontPayment().setScale(2).toString() : "0.00"
        ));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionStatus() != null ? cellData.getValue().getSessionStatus().name() : "N/A"
        ));

        tblSessions.setItems(sessionList);
        loadMapsAndSessions();
    }

    private void loadMapsAndSessions() {
        patientMap.clear();
        therapistMap.clear();
        programMap.clear();
        sessionList.clear();

        try {
            // Load lookups
            List<PatientDTO> patients = patientBO.getAllPatients();
            for (PatientDTO p : patients) {
                patientMap.put(p.getPatientId(), p.getFirstName() + " " + p.getLastName());
            }

            List<TherapistDTO> therapists = therapistBO.getAllTherapists();
            for (TherapistDTO t : therapists) {
                therapistMap.put(t.getTherapistId(), t.getTherapistFirstName() + " " + t.getTherapistLastName());
            }

            List<TherapyProgramDTO> programs = programBO.getAllTherapyPrograms();
            for (TherapyProgramDTO pr : programs) {
                programMap.put(pr.getProgramId(), pr.getProgramCode() + " - " + pr.getProgramName());
            }

            // Load sessions
            List<TherapySessionDTO> sessions = sessionBO.getAllSessions();
            sessionList.addAll(sessions);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load session details: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            loadMapsAndSessions();
            return;
        }
        query = query.trim().toLowerCase();

        String finalQuery = query;
        List<TherapySessionDTO> filtered = sessionList.stream()
                .filter(s -> {
                    String patName = patientMap.getOrDefault(s.getPatientId(), "").toLowerCase();
                    String therName = therapistMap.getOrDefault(s.getTherapistId(), "").toLowerCase();
                    String progName = programMap.getOrDefault(s.getProgramId(), "").toLowerCase();
                    return s.getSessionId().equals(finalQuery) ||
                            patName.contains(finalQuery) ||
                            therName.contains(finalQuery) ||
                            progName.contains(finalQuery);
                })
                .collect(Collectors.toList());

        sessionList.setAll(filtered);
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadMapsAndSessions();
    }

    @FXML
    void handleBookSession(ActionEvent event) {
        // Open Book Session Dialog
        List<PatientDTO> patients;
        List<TherapistDTO> therapists;
        List<TherapyProgramDTO> programs;

        try {
            patients = patientBO.getAllPatients();
            therapists = therapistBO.getAllTherapists();
            programs = programBO.getAllTherapyPrograms();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load booking dependencies: " + e.getMessage()).show();
            return;
        }

        if (patients.isEmpty() || therapists.isEmpty() || programs.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cannot book session. Ensure patients, therapists, and therapy programs exist in the system.").show();
            return;
        }

        Dialog<TherapySessionDTO> dialog = new Dialog<>();
        dialog.setTitle("Book Therapy Session");
        dialog.setHeaderText("Enter New Session Appointment Details");

        ButtonType saveButtonType = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<PatientDTO> patientCombo = new ComboBox<>();
        patientCombo.getItems().addAll(patients);
        patientCombo.setPromptText("Select Patient");
        patientCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PatientDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : "ID: " + item.getPatientId() + " - " + item.getFirstName() + " " + item.getLastName());
            }
        });
        patientCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PatientDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : "ID: " + item.getPatientId() + " - " + item.getFirstName() + " " + item.getLastName());
            }
        });

        ComboBox<TherapistDTO> therapistCombo = new ComboBox<>();
        therapistCombo.getItems().addAll(therapists);
        therapistCombo.setPromptText("Select Therapist");
        therapistCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TherapistDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : "ID: " + item.getTherapistId() + " - " + item.getTherapistFirstName() + " " + item.getTherapistLastName());
            }
        });
        therapistCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TherapistDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : "ID: " + item.getTherapistId() + " - " + item.getTherapistFirstName() + " " + item.getTherapistLastName());
            }
        });

        ComboBox<TherapyProgramDTO> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(programs);
        programCombo.setPromptText("Select Program");
        programCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TherapyProgramDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getProgramCode() + " - " + item.getProgramName());
            }
        });
        programCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TherapyProgramDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getProgramCode() + " - " + item.getProgramName());
            }
        });

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField("10:00");
        timeField.setPromptText("HH:mm (24-hour format)");
        TextArea noteField = new TextArea();
        noteField.setPrefRowCount(3);
        TextField upfrontField = new TextField("0.00");
        upfrontField.setPromptText("Upfront Amount (LKR)");

        // Auto calculate fee suggestion when program selected
        programCombo.setOnAction(e -> {
            TherapyProgramDTO selectedProg = programCombo.getValue();
            if (selectedProg != null && selectedProg.getProgramFee() != null) {
                upfrontField.setText(selectedProg.getProgramFee().toString());
            }
        });

        grid.add(new Label("Patient:"), 0, 0);
        grid.add(patientCombo, 1, 0);
        grid.add(new Label("Therapist:"), 0, 1);
        grid.add(therapistCombo, 1, 1);
        grid.add(new Label("Program:"), 0, 2);
        grid.add(programCombo, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Time (HH:mm):"), 0, 4);
        grid.add(timeField, 1, 4);
        grid.add(new Label("Notes:"), 0, 5);
        grid.add(noteField, 1, 5);
        grid.add(new Label("Upfront Amount (LKR):"), 0, 6);
        grid.add(upfrontField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Button btnBook = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        btnBook.addEventFilter(ActionEvent.ACTION, eventFilter -> {
            if (patientCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a Patient!").showAndWait();
                eventFilter.consume();
                return;
            }
            if (therapistCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a Therapist!").showAndWait();
                eventFilter.consume();
                return;
            }
            if (programCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a Therapy Program!").showAndWait();
                eventFilter.consume();
                return;
            }
            if (datePicker.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a Session Date!").showAndWait();
                eventFilter.consume();
                return;
            }
            if (timeField.getText() == null || timeField.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter a Session Time!").showAndWait();
                eventFilter.consume();
                return;
            }
            try {
                LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException ex) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid Time in HH:mm format!").showAndWait();
                eventFilter.consume();
                return;
            }

            if (upfrontField.getText() == null || upfrontField.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter an Upfront Amount!").showAndWait();
                eventFilter.consume();
                return;
            }
            try {
                BigDecimal amount = new BigDecimal(upfrontField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    new Alert(Alert.AlertType.WARNING, "Upfront Amount cannot be negative!").showAndWait();
                    eventFilter.consume();
                    return;
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid Upfront Amount!").showAndWait();
                eventFilter.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (patientCombo.getValue() == null || therapistCombo.getValue() == null || programCombo.getValue() == null) {
                    return null;
                }
                LocalTime parsedTime;
                try {
                    parsedTime = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                } catch (DateTimeParseException ex) {
                    parsedTime = LocalTime.of(10, 0); // default
                }

                BigDecimal upfrontAmount;
                try {
                    upfrontAmount = new BigDecimal(upfrontField.getText().trim());
                } catch (NumberFormatException ex) {
                    upfrontAmount = BigDecimal.ZERO;
                }

                TherapySessionDTO dto = new TherapySessionDTO();
                dto.setPatientId(patientCombo.getValue().getPatientId());
                dto.setTherapistId(therapistCombo.getValue().getTherapistId());
                dto.setProgramId(programCombo.getValue().getProgramId());
                dto.setSessionDate(datePicker.getValue());
                dto.setSessionTime(parsedTime);
                dto.setSessionStatus(SessionStatus.SCHEDULED);
                dto.setNote(noteField.getText());
                dto.setUpfrontPayment(upfrontAmount);
                return dto;
            }
            return null;
        });

        Optional<TherapySessionDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                boolean success = sessionBO.saveSession(dto);
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Session booked successfully.").show();
                    loadMapsAndSessions();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to book session.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Booking Error: " + e.getMessage()).show();
            }
        });
    }

    @FXML
    void handleCompleteSession(ActionEvent event) {
        TherapySessionDTO selected = tblSessions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a session to complete.").show();
            return;
        }

        if (selected.getSessionStatus() != SessionStatus.SCHEDULED) {
            new Alert(Alert.AlertType.WARNING, "Only SCHEDULED sessions can be marked as completed.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Mark Session #" + selected.getSessionId() + " as COMPLETED?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                selected.setSessionStatus(SessionStatus.COMPLETED);
                boolean success = sessionBO.updateSession(selected);
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Session marked as completed.").show();
                    loadMapsAndSessions();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update session.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void handleCancelSession(ActionEvent event) {
        TherapySessionDTO selected = tblSessions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a session to cancel.").show();
            return;
        }

        if (selected.getSessionStatus() != SessionStatus.SCHEDULED) {
            new Alert(Alert.AlertType.WARNING, "Only SCHEDULED sessions can be cancelled.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to CANCEL Session #" + selected.getSessionId() + "? This will refund the session credit.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                selected.setSessionStatus(SessionStatus.CANCELLED);
                boolean success = sessionBO.updateSession(selected);
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Session cancelled and credit refunded.").show();
                    loadMapsAndSessions();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to cancel session.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }
}
