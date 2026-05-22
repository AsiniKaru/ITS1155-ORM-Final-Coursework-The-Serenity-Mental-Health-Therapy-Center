package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientMgtController {

    @FXML private TextField txtSearch;
    @FXML private TableView<PatientDTO> tblPatients;
    @FXML private TableColumn<PatientDTO, String> colId;
    @FXML private TableColumn<PatientDTO, String> colName;
    @FXML private TableColumn<PatientDTO, String> colEmail;
    @FXML private TableColumn<PatientDTO, String> colAddress;
    @FXML private TableColumn<PatientDTO, String> colPhone;
    @FXML private TableColumn<PatientDTO, String> colMedical;

    @FXML private TableView<TherapySessionDTO> tblHistory;
    @FXML private TableColumn<TherapySessionDTO, String> colHistDate;
    @FXML private TableColumn<TherapySessionDTO, String> colHistProgram;
    @FXML private TableColumn<TherapySessionDTO, String> colHistTherapist;
    @FXML private TableColumn<TherapySessionDTO, String> colHistStatus;
    @FXML private TableColumn<TherapySessionDTO, String> colHistNotes;

    private final PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    private final ObservableList<PatientDTO> patientList = FXCollections.observableArrayList();
    private final ObservableList<TherapySessionDTO> historyList = FXCollections.observableArrayList();

    private final Map<String, String> therapistNames = new HashMap<>();
    private final Map<String, String> programNames = new HashMap<>();

    @FXML
    public void initialize() {
        loadMappings();

        // Bind Patient Columns
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientId()));
        colName.setCellValueFactory(cellData -> {
            String first = cellData.getValue().getFirstName() != null ? cellData.getValue().getFirstName() : "";
            String last = cellData.getValue().getLastName() != null ? cellData.getValue().getLastName() : "";
            return new SimpleStringProperty((first + " " + last).trim());
        });
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : ""));
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress() != null ? cellData.getValue().getAddress() : ""));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber() != null ? cellData.getValue().getPhoneNumber() : ""));
        colMedical.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicalHistory() != null ? cellData.getValue().getMedicalHistory() : ""));

        // Bind History Columns
        colHistDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionDate() != null ? cellData.getValue().getSessionDate().toString() : "N/A"
        ));
        colHistProgram.setCellValueFactory(cellData -> {
            String progId = cellData.getValue().getProgramId();
            if (progId == null) return new SimpleStringProperty("N/A");
            return new SimpleStringProperty(programNames.getOrDefault(progId, "Program ID: " + progId));
        });
        colHistTherapist.setCellValueFactory(cellData -> {
            String tId = cellData.getValue().getTherapistId();
            if (tId == null) return new SimpleStringProperty("N/A");
            return new SimpleStringProperty(therapistNames.getOrDefault(tId, "Therapist ID: " + tId));
        });
        colHistStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionStatus() != null ? cellData.getValue().getSessionStatus().name() : "N/A"
        ));
        colHistNotes.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNote() != null ? cellData.getValue().getNote() : ""
        ));

        tblPatients.setItems(patientList);
        tblHistory.setItems(historyList);

        loadAllPatients();
    }

    private void loadMappings() {
        try {
            therapistNames.clear();
            for (TherapistDTO t : therapistBO.getAllTherapists()) {
                if (t.getTherapistId() != null) {
                    therapistNames.put(t.getTherapistId(), (t.getTherapistFirstName() != null ? t.getTherapistFirstName() : "") + " " + (t.getTherapistLastName() != null ? t.getTherapistLastName() : ""));
                }
            }

            programNames.clear();
            for (TherapyProgramDTO p : programBO.getAllTherapyPrograms()) {
                if (p.getProgramId() != null) {
                    programNames.put(p.getProgramId(), p.getProgramName() != null ? p.getProgramName() : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllPatients() {
        patientList.clear();
        try {
            List<PatientDTO> allPatients = patientBO.getAllPatients();
            patientList.addAll(allPatients);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load patients: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            loadAllPatients();
            return;
        }
        query = query.trim().toLowerCase();
        
        String finalQuery = query;
        List<PatientDTO> filtered = patientList.stream()
                .filter(p -> (p.getPatientId() != null && p.getPatientId().toLowerCase().contains(finalQuery)) ||
                        (p.getFirstName() != null && p.getFirstName().toLowerCase().contains(finalQuery)) ||
                        (p.getLastName() != null && p.getLastName().toLowerCase().contains(finalQuery)))
                .collect(Collectors.toList());
        
        patientList.setAll(filtered);
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadAllPatients();
        historyList.clear();
    }

    @FXML
    void handleAddPatient(ActionEvent event) {
        showPatientForm(null);
    }

    @FXML
    void handleEditPatient(ActionEvent event) {
        PatientDTO selected = tblPatients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient to edit.").show();
            return;
        }
        showPatientForm(selected);
    }

    @FXML
    void handleDeletePatient(ActionEvent event) {
        PatientDTO selected = tblPatients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient to delete.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove patient " + selected.getFirstName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean success = patientBO.deletePatient(selected.getPatientId());
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Patient deleted successfully.").show();
                    loadAllPatients();
                    historyList.clear();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete patient.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void handlePatientTableClick(MouseEvent event) {
        PatientDTO selected = tblPatients.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadPatientHistory(selected.getPatientId());
        }
    }

    private void loadPatientHistory(String patientId) {
        historyList.clear();
        try {
            loadMappings();
            List<TherapySessionDTO> sessions = sessionBO.getAllSessions().stream()
                    .filter(s -> patientId != null && patientId.equals(s.getPatientId()))
                    .collect(Collectors.toList());
            historyList.addAll(sessions);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load therapy history: " + e.getMessage()).show();
        }
    }

    private void showPatientForm(PatientDTO existing) {
        Dialog<PatientDTO> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Patient" : "Edit Patient");
        dialog.setHeaderText(existing == null ? "Enter Patient Details" : "Modify Patient Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField address = new TextField();
        TextArea medicalHistory = new TextArea();
        medicalHistory.setPrefRowCount(3);

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phone, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(address, 1, 4);
        grid.add(new Label("Medical History:"), 0, 5);
        grid.add(medicalHistory, 1, 5);

        if (existing != null) {
            firstName.setText(existing.getFirstName());
            lastName.setText(existing.getLastName());
            email.setText(existing.getEmail());
            phone.setText(existing.getPhoneNumber());
            address.setText(existing.getAddress());
            medicalHistory.setText(existing.getMedicalHistory());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                PatientDTO dto = new PatientDTO();
                if (existing != null) {
                    dto.setPatientId(existing.getPatientId());
                    dto.setRegisteredDate(existing.getRegisteredDate());
                } else {
                    dto.setRegisteredDate(LocalDate.now());
                }
                dto.setFirstName(firstName.getText());
                dto.setLastName(lastName.getText());
                dto.setEmail(email.getText());
                dto.setPhoneNumber(phone.getText());
                dto.setAddress(address.getText());
                dto.setMedicalHistory(medicalHistory.getText());
                return dto;
            }
            return null;
        });

        Optional<PatientDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                boolean success;
                if (existing == null) {
                    success = patientBO.savePatient(dto);
                } else {
                    success = patientBO.updatePatient(dto);
                }
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Patient saved successfully.").show();
                    loadAllPatients();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save patient details.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Validation/Database Error: " + e.getMessage()).show();
            }
        });
    }
}
