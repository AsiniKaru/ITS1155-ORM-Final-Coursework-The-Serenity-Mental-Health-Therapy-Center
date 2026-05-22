package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ReportUtil;

public class AdminSessionReportController {

    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;
    @FXML private ChoiceBox<String> choiceStatus;

    @FXML private TableView<TherapySessionDTO> tblSessions;
    @FXML private TableColumn<TherapySessionDTO, String> colId;
    @FXML private TableColumn<TherapySessionDTO, String> colPatient;
    @FXML private TableColumn<TherapySessionDTO, String> colTherapist;
    @FXML private TableColumn<TherapySessionDTO, String> colProgram;
    @FXML private TableColumn<TherapySessionDTO, String> colDate;
    @FXML private TableColumn<TherapySessionDTO, String> colTime;
    @FXML private TableColumn<TherapySessionDTO, String> colStatus;
    @FXML private TableColumn<TherapySessionDTO, String> colNotes;

    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    private final ObservableList<TherapySessionDTO> sessionList = FXCollections.observableArrayList();

    private final Map<String, String> patientNames = new HashMap<>();
    private final Map<String, String> therapistNames = new HashMap<>();
    private final Map<String, String> programNames = new HashMap<>();

    @FXML
    public void initialize() {
        // Populate Status choices
        choiceStatus.setItems(FXCollections.observableArrayList("ALL", "SCHEDULED", "COMPLETED", "CANCELLED"));
        choiceStatus.setValue("ALL");

        // Load mappings
        loadMappings();

        // Bind columns
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSessionId()));
        colPatient.setCellValueFactory(cellData -> {
            String pId = cellData.getValue().getPatientId();
            return new SimpleStringProperty(patientNames.getOrDefault(pId, "Patient ID: " + pId));
        });
        colTherapist.setCellValueFactory(cellData -> {
            String tId = cellData.getValue().getTherapistId();
            return new SimpleStringProperty(therapistNames.getOrDefault(tId, "Therapist ID: " + tId));
        });
        colProgram.setCellValueFactory(cellData -> {
            String prgId = cellData.getValue().getProgramId();
            return new SimpleStringProperty(programNames.getOrDefault(prgId, "Program ID: " + prgId));
        });
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionDate() != null ? cellData.getValue().getSessionDate().toString() : "N/A"
        ));
        colTime.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionTime() != null ? cellData.getValue().getSessionTime().toString() : "N/A"
        ));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSessionStatus() != null ? cellData.getValue().getSessionStatus().name() : "N/A"
        ));
        colNotes.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));

        tblSessions.setItems(sessionList);

        loadAllSessions();
    }

    private void loadMappings() {
        try {
            patientNames.clear();
            for (PatientDTO p : patientBO.getAllPatients()) {
                patientNames.put(p.getPatientId(), p.getFirstName() + " " + p.getLastName());
            }

            therapistNames.clear();
            for (TherapistDTO t : therapistBO.getAllTherapists()) {
                therapistNames.put(t.getTherapistId(), t.getTherapistFirstName() + " " + t.getTherapistLastName());
            }

            programNames.clear();
            for (TherapyProgramDTO prg : programBO.getAllTherapyPrograms()) {
                programNames.put(prg.getProgramId(), prg.getProgramName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllSessions() {
        sessionList.clear();
        try {
            List<TherapySessionDTO> all = sessionBO.getAllSessions();
            sessionList.addAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load sessions: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleGenerateReport(ActionEvent event) {
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        String status = choiceStatus.getValue();

        try {
            loadMappings(); // Refresh mappings
            List<TherapySessionDTO> all = sessionBO.getAllSessions();

            List<TherapySessionDTO> filtered = all.stream().filter(s -> {
                if (s.getSessionDate() == null) {
                    boolean matchStatus = "ALL".equals(status) || (s.getSessionStatus() != null && s.getSessionStatus().name().equals(status));
                    return from == null && to == null && matchStatus;
                }
                boolean matchFrom = (from == null) || !s.getSessionDate().isBefore(from);
                boolean matchTo = (to == null) || !s.getSessionDate().isAfter(to);
                boolean matchStatus = "ALL".equals(status) || (s.getSessionStatus() != null && s.getSessionStatus().name().equals(status));
                return matchFrom && matchTo && matchStatus;
            }).collect(Collectors.toList());

            sessionList.setAll(filtered);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate report: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleReset(ActionEvent event) {
        dpFrom.setValue(null);
        dpTo.setValue(null);
        choiceStatus.setValue("ALL");
        loadAllSessions();
    }

    @FXML
    void handlePrintReport(ActionEvent event) {
        try {
            LocalDate from = dpFrom.getValue();
            LocalDate to = dpTo.getValue();
            String status = choiceStatus.getValue();

            Map<String, Object> params = new HashMap<>();
            params.put("startDate", from != null ? from.toString() : "All Time");
            params.put("endDate", to != null ? to.toString() : "All Time");
            params.put("filterStatus", status != null ? status : "ALL");

            List<SessionSummaryRow> rows = sessionList.stream().map(s -> new SessionSummaryRow(
                    s.getSessionId(),
                    patientNames.getOrDefault(s.getPatientId(), "Patient ID: " + s.getPatientId()),
                    therapistNames.getOrDefault(s.getTherapistId(), "Therapist ID: " + s.getTherapistId()),
                    programNames.getOrDefault(s.getProgramId(), "Program ID: " + s.getProgramId()),
                    s.getSessionDate() != null ? s.getSessionDate().toString() : "N/A",
                    s.getSessionTime() != null ? s.getSessionTime().toString() : "N/A",
                    s.getSessionStatus() != null ? s.getSessionStatus().name() : "N/A"
            )).collect(Collectors.toList());

            ReportUtil.showReport("/reports/session_summary.jrxml", params, rows);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to print session summary report: " + e.getMessage()).show();
        }
    }

    public static class SessionSummaryRow {
        private final String sessionId;
        private final String patientName;
        private final String therapistName;
        private final String programName;
        private final String sessionDate;
        private final String sessionTime;
        private final String sessionStatus;

        public SessionSummaryRow(String sessionId, String patientName, String therapistName, String programName, String sessionDate, String sessionTime, String sessionStatus) {
            this.sessionId = sessionId;
            this.patientName = patientName;
            this.therapistName = therapistName;
            this.programName = programName;
            this.sessionDate = sessionDate;
            this.sessionTime = sessionTime;
            this.sessionStatus = sessionStatus;
        }

        public String getSessionId() { return sessionId; }
        public String getPatientName() { return patientName; }
        public String getTherapistName() { return therapistName; }
        public String getProgramName() { return programName; }
        public String getSessionDate() { return sessionDate; }
        public String getSessionTime() { return sessionTime; }
        public String getSessionStatus() { return sessionStatus; }
    }
}
