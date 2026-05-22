package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientTherapyHistoryController {

    @FXML private TextField txtSearch;
    @FXML private TableView<PatientDTO> tblPatients;
    @FXML private TableColumn<PatientDTO, String> colId;
    @FXML private TableColumn<PatientDTO, String> colName;
    @FXML private TableColumn<PatientDTO, String> colEmail;
    @FXML private TableColumn<PatientDTO, String> colAddress;
    @FXML private TableColumn<PatientDTO, String> colPhone;

    @FXML private TableView<TherapySessionDTO> tblHistory;
    @FXML private TableColumn<TherapySessionDTO, String> colHistDate;
    @FXML private TableColumn<TherapySessionDTO, String> colHistSession;
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

        // Bind Patient Table
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientId()));
        colName.setCellValueFactory(cellData -> {
            String first = cellData.getValue().getFirstName() != null ? cellData.getValue().getFirstName() : "";
            String last = cellData.getValue().getLastName() != null ? cellData.getValue().getLastName() : "";
            return new SimpleStringProperty((first + " " + last).trim());
        });
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : ""));
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress() != null ? cellData.getValue().getAddress() : ""));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber() != null ? cellData.getValue().getPhoneNumber() : ""));

        // Bind History Table
        colHistDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionDate() != null ? cellData.getValue().getSessionDate().toString() : "N/A"
        ));
        colHistSession.setCellValueFactory(cellData -> {
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
            List<PatientDTO> all = patientBO.getAllPatients();
            patientList.addAll(all);
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
        try {
            List<PatientDTO> filtered = patientBO.getAllPatients().stream()
                    .filter(p -> (p.getPatientId() != null && p.getPatientId().toLowerCase().contains(finalQuery)) ||
                            (p.getFirstName() != null && p.getFirstName().toLowerCase().contains(finalQuery)) ||
                            (p.getLastName() != null && p.getLastName().toLowerCase().contains(finalQuery)))
                    .collect(Collectors.toList());
            patientList.setAll(filtered);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error searching patients: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadAllPatients();
        historyList.clear();
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
            loadMappings(); // Refresh mappings
            List<TherapySessionDTO> sessions = sessionBO.getAllSessions().stream()
                    .filter(s -> patientId != null && patientId.equals(s.getPatientId()))
                    .collect(Collectors.toList());
            historyList.addAll(sessions);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load therapy history: " + e.getMessage()).show();
        }
    }
}
