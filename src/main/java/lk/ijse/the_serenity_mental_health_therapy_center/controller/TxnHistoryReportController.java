package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PaymentBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PaymentDTo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ReportUtil;

public class TxnHistoryReportController {

    @FXML private DatePicker dpTxnDate;

    @FXML private TableView<PaymentDTo> tblTxn;
    @FXML private TableColumn<PaymentDTo, String> colId;
    @FXML private TableColumn<PaymentDTo, String> colInvoice;
    @FXML private TableColumn<PaymentDTo, String> colPatient;
    @FXML private TableColumn<PaymentDTo, String> colTime;
    @FXML private TableColumn<PaymentDTo, String> colMethod;
    @FXML private TableColumn<PaymentDTo, String> colStatus;
    @FXML private TableColumn<PaymentDTo, String> colAmount;

    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);

    private final ObservableList<PaymentDTo> paymentList = FXCollections.observableArrayList();
    private final Map<String, String> patientNames = new HashMap<>();

    @FXML
    public void initialize() {
        loadPatientMappings();

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaymentId()));
        colInvoice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoiceNumber()));
        colPatient.setCellValueFactory(cellData -> {
            String pId = cellData.getValue().getId(); // Patient ID is stored in 'id' field in PaymentDTo
            return new SimpleStringProperty(patientNames.getOrDefault(pId, "Patient ID: " + pId));
        });
        colTime.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaymentDate() != null ? cellData.getValue().getPaymentDate().toString() : "N/A"
        ));
        colMethod.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaymentMethod() != null ? cellData.getValue().getPaymentMethod().name() : "N/A"
        ));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaymentStatus() != null ? cellData.getValue().getPaymentStatus().name() : "N/A"
        ));
        colAmount.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAmount() != null ? "Rs. " + cellData.getValue().getAmount().setScale(2).toString() : "Rs. 0.00"
        ));

        tblTxn.setItems(paymentList);

        generateReport(null);
    }

    private void loadPatientMappings() {
        try {
            patientNames.clear();
            for (PatientDTO p : patientBO.getAllPatients()) {
                patientNames.put(p.getPatientId(), p.getFirstName() + " " + p.getLastName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateReport(LocalDate date) {
        paymentList.clear();
        try {
            loadPatientMappings();
            List<PaymentDTo> all = paymentBO.getAllPayments();

            List<PaymentDTo> filtered = all.stream().filter(p -> {
                if (p.getPaymentDate() == null) return false;
                if (date == null) return true;
                return p.getPaymentDate().toLocalDate().equals(date);
            }).collect(Collectors.toList());

            paymentList.addAll(filtered);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load transaction history: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleGenerateReport(ActionEvent event) {
        generateReport(dpTxnDate.getValue());
    }

    @FXML
    void handleReset(ActionEvent event) {
        dpTxnDate.setValue(null);
        generateReport(null);
    }

    @FXML
    void handlePrintReport(ActionEvent event) {
        try {
            LocalDate date = dpTxnDate.getValue();

            Map<String, Object> params = new HashMap<>();
            params.put("reportDate", date != null ? date.toString() : "All Transactions");

            List<TxnHistoryReportRow> rows = paymentList.stream().map(p -> new TxnHistoryReportRow(
                    p.getPaymentId(),
                    p.getInvoiceNumber(),
                    patientNames.getOrDefault(p.getId(), "Patient ID: " + p.getId()),
                    p.getPaymentDate() != null ? p.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                    p.getPaymentMethod() != null ? p.getPaymentMethod().name() : "N/A",
                    p.getPaymentStatus() != null ? p.getPaymentStatus().name() : "N/A",
                    p.getAmount() != null ? p.getAmount().doubleValue() : 0.0
            )).collect(Collectors.toList());

            ReportUtil.showReport("/reports/txn_history_report.jrxml", params, rows);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to print transaction history: " + e.getMessage()).show();
        }
    }

    public static class TxnHistoryReportRow {
        private final String paymentId;
        private final String invoiceNumber;
        private final String patientName;
        private final String paymentDate;
        private final String paymentMethod;
        private final String paymentStatus;
        private final Double amount;

        public TxnHistoryReportRow(String paymentId, String invoiceNumber, String patientName, String paymentDate, String paymentMethod, String paymentStatus, Double amount) {
            this.paymentId = paymentId;
            this.invoiceNumber = invoiceNumber;
            this.patientName = patientName;
            this.paymentDate = paymentDate;
            this.paymentMethod = paymentMethod;
            this.paymentStatus = paymentStatus;
            this.amount = amount;
        }

        public String getPaymentId() { return paymentId; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public String getPatientName() { return patientName; }
        public String getPaymentDate() { return paymentDate; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public Double getAmount() { return amount; }
    }
}
