package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ReportUtil;

public class TherapistPerformanceReportController {

    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    @FXML private TableView<TherapistPerformanceRow> tblPerformance;
    @FXML private TableColumn<TherapistPerformanceRow, String> colId;
    @FXML private TableColumn<TherapistPerformanceRow, String> colName;
    @FXML private TableColumn<TherapistPerformanceRow, String> colSpecialization;
    @FXML private TableColumn<TherapistPerformanceRow, String> colAvailability;
    @FXML private TableColumn<TherapistPerformanceRow, Number> colSessionCount;

    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);

    private final ObservableList<TherapistPerformanceRow> performanceRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTherapistId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colSpecialization.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialization()));
        colAvailability.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailability()));
        colSessionCount.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSessionCount()));

        tblPerformance.setItems(performanceRows);

        generateReport(null, null);
    }

    private void generateReport(LocalDate from, LocalDate to) {
        performanceRows.clear();
        try {
            List<TherapistDTO> therapists = therapistBO.getAllTherapists();
            List<TherapySessionDTO> sessions = sessionBO.getAllSessions();

            // Filter sessions by date range if provided
            List<TherapySessionDTO> filteredSessions = sessions.stream().filter(s -> {
                if (s.getSessionDate() == null) {
                    return (from == null) && (to == null);
                }
                boolean matchFrom = (from == null) || !s.getSessionDate().isBefore(from);
                boolean matchTo = (to == null) || !s.getSessionDate().isAfter(to);
                return matchFrom && matchTo;
            }).collect(Collectors.toList());

            List<TherapistPerformanceRow> rows = new ArrayList<>();
            for (TherapistDTO therapist : therapists) {
                long count = filteredSessions.stream()
                        .filter(s -> s.getTherapistId() != null && s.getTherapistId().equals(therapist.getTherapistId()))
                        .count();

                rows.add(new TherapistPerformanceRow(
                        therapist.getTherapistId(),
                        (therapist.getTherapistFirstName() != null ? therapist.getTherapistFirstName() : "") + " " + (therapist.getTherapistLastName() != null ? therapist.getTherapistLastName() : ""),
                        therapist.getSpecialization(),
                        therapist.getAvailability() != null ? therapist.getAvailability().name() : "N/A",
                        (int) count
                ));
            }

            performanceRows.addAll(rows);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load performance report: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleGenerateReport(ActionEvent event) {
        generateReport(dpFrom.getValue(), dpTo.getValue());
    }

    @FXML
    void handleReset(ActionEvent event) {
        dpFrom.setValue(null);
        dpTo.setValue(null);
        generateReport(null, null);
    }

    @FXML
    void handlePrintReport(ActionEvent event) {
        try {
            LocalDate from = dpFrom.getValue();
            LocalDate to = dpTo.getValue();

            Map<String, Object> params = new HashMap<>();
            params.put("startDate", from != null ? from.toString() : "All Time");
            params.put("endDate", to != null ? to.toString() : "All Time");

            ReportUtil.showReport("/reports/therapist_performance.jrxml", params, performanceRows);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to print therapist performance report: " + e.getMessage()).show();
        }
    }

    public static class TherapistPerformanceRow {
        private final String therapistId;
        private final String name;
        private final String specialization;
        private final String availability;
        private final int sessionCount;

        public TherapistPerformanceRow(String therapistId, String name, String specialization, String availability, int sessionCount) {
            this.therapistId = therapistId;
            this.name = name;
            this.specialization = specialization;
            this.availability = availability;
            this.sessionCount = sessionCount;
        }

        public String getTherapistId() { return therapistId; }
        public String getName() { return name; }
        public String getSpecialization() { return specialization; }
        public String getAvailability() { return availability; }
        public int getSessionCount() { return sessionCount; }
    }
}
