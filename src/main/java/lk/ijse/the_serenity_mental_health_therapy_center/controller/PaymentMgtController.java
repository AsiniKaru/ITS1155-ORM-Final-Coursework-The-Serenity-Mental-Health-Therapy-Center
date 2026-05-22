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
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PaymentBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PaymentDTo;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.PaymentMethod;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.PaymentStatus;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.SessionStatus;

import lk.ijse.the_serenity_mental_health_therapy_center.util.ReportUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaymentMgtController {

    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;
    @FXML private ChoiceBox<String> cbStatus;
    @FXML private TableView<PaymentDTo> tblPayments;
    @FXML private TableColumn<PaymentDTo, String> colId;
    @FXML private TableColumn<PaymentDTo, String> colInvoice;
    @FXML private TableColumn<PaymentDTo, String> colPatient;
    @FXML private TableColumn<PaymentDTo, String> colAmount;
    @FXML private TableColumn<PaymentDTo, String> colDate;
    @FXML private TableColumn<PaymentDTo, String> colMethod;
    @FXML private TableColumn<PaymentDTo, String> colStatus;

    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);
    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);

    private final ObservableList<PaymentDTo> paymentList = FXCollections.observableArrayList();
    private final Map<String, String> patientMap = new HashMap<>();
    private final Map<String, BigDecimal> programFeeMap = new HashMap<>();
    private final Map<String, String> programNameMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Populate Status Filter
        cbStatus.getItems().add("ALL");
        for (PaymentStatus status : PaymentStatus.values()) {
            cbStatus.getItems().add(status.name());
        }
        cbStatus.setValue("ALL");

        // Bind columns
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaymentId()));
        colInvoice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoiceNumber()));
        colPatient.setCellValueFactory(cellData -> {
            String patientId = cellData.getValue().getId();
            return new SimpleStringProperty(patientMap.getOrDefault(patientId, "Patient ID: " + patientId));
        });
        colAmount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAmount() != null ? cellData.getValue().getAmount().toString() : "0.00"));
        colDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getPaymentDate();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
        });
        colMethod.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaymentMethod() != null ? cellData.getValue().getPaymentMethod().name() : "N/A"
        ));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPaymentStatus() != null ? cellData.getValue().getPaymentStatus().name() : "N/A"
        ));

        tblPayments.setItems(paymentList);
        loadMapsAndPayments();
    }

    private void loadMapsAndPayments() {
        patientMap.clear();
        paymentList.clear();
        programFeeMap.clear();
        programNameMap.clear();
        try {
            List<PatientDTO> patients = patientBO.getAllPatients();
            for (PatientDTO p : patients) {
                patientMap.put(p.getPatientId(), p.getFirstName() + " " + p.getLastName());
            }

            List<TherapyProgramDTO> programs = programBO.getAllTherapyPrograms();
            for (TherapyProgramDTO pr : programs) {
                programFeeMap.put(pr.getProgramId(), pr.getProgramFee());
                programNameMap.put(pr.getProgramId(), pr.getProgramCode() + " - " + pr.getProgramName());
            }

            List<PaymentDTo> allPayments = paymentBO.getAllPayments();
            paymentList.addAll(allPayments);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load payment info: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        String statusFilter = cbStatus.getValue();

        List<PaymentDTo> filtered = paymentList.stream()
                .filter(p -> {
                    LocalDate pDate = p.getPaymentDate().toLocalDate();
                    if (from != null && pDate.isBefore(from)) return false;
                    if (to != null && pDate.isAfter(to)) return false;
                    if (!"ALL".equals(statusFilter) && !p.getPaymentStatus().name().equals(statusFilter)) return false;
                    return true;
                })
                .collect(Collectors.toList());

        tblPayments.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    void handleReset(ActionEvent event) {
        dpFrom.setValue(null);
        dpTo.setValue(null);
        cbStatus.setValue("ALL");
        tblPayments.setItems(paymentList);
        loadMapsAndPayments();
    }

    @FXML
    void handleRecordPayment(ActionEvent event) {
        List<TherapySessionDTO> bookedSessions;
        try {
            bookedSessions = sessionBO.getAllSessions().stream()
                    .filter(s -> s.getSessionStatus() == SessionStatus.SCHEDULED && 
                                 (s.getPaymentStatus() == null || !"COMPLETED".equals(s.getPaymentStatus())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load booked sessions: " + e.getMessage()).show();
            return;
        }

        if (bookedSessions.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No booked sessions available for payment collection.").show();
            return;
        }

        Dialog<PaymentDTo> dialog = new Dialog<>();
        dialog.setTitle("Record New Payment");
        dialog.setHeaderText("Select Booked Session to Collect Remaining/Full Payment");

        ButtonType saveButtonType = new ButtonType("Record", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<TherapySessionDTO> sessionCombo = new ComboBox<>();
        sessionCombo.getItems().addAll(bookedSessions);
        sessionCombo.setPromptText("Select Booked Session");
        sessionCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TherapySessionDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String patientName = patientMap.getOrDefault(item.getPatientId(), "Patient ID: " + item.getPatientId());
                    String programName = programNameMap.getOrDefault(item.getProgramId(), "Program ID: " + item.getProgramId());
                    setText("Session #" + item.getSessionId() + " - " + patientName + " (" + programName + ")");
                }
            }
        });
        sessionCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TherapySessionDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String patientName = patientMap.getOrDefault(item.getPatientId(), "Patient ID: " + item.getPatientId());
                    String programName = programNameMap.getOrDefault(item.getProgramId(), "Program ID: " + item.getProgramId());
                    setText("Session #" + item.getSessionId() + " - " + patientName + " (" + programName + ")");
                }
            }
        });

        Label lblPatient = new Label("-");
        Label lblProgram = new Label("-");
        Label lblUpfront = new Label("0.00");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount in LKR");

        ComboBox<PaymentMethod> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll(PaymentMethod.values());
        methodCombo.setValue(PaymentMethod.CASH);

        ComboBox<PaymentStatus> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(PaymentStatus.values());
        statusCombo.setValue(PaymentStatus.COMPLETED);

        TextField invoiceField = new TextField("INV-" + System.currentTimeMillis() / 1000);
        TextField remarkField = new TextField();

        grid.add(new Label("Booked Session:"), 0, 0);
        grid.add(sessionCombo, 1, 0);
        grid.add(new Label("Patient:"), 0, 1);
        grid.add(lblPatient, 1, 1);
        grid.add(new Label("Program:"), 0, 2);
        grid.add(lblProgram, 1, 2);
        grid.add(new Label("Upfront Paid (LKR):"), 0, 3);
        grid.add(lblUpfront, 1, 3);
        grid.add(new Label("Remaining Amount (LKR):"), 0, 4);
        grid.add(amountField, 1, 4);
        grid.add(new Label("Method:"), 0, 5);
        grid.add(methodCombo, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);
        grid.add(new Label("Invoice Number:"), 0, 7);
        grid.add(invoiceField, 1, 7);
        grid.add(new Label("Remarks:"), 0, 8);
        grid.add(remarkField, 1, 8);

        sessionCombo.setOnAction(e -> {
            TherapySessionDTO selectedSession = sessionCombo.getValue();
            if (selectedSession != null) {
                String patientName = patientMap.getOrDefault(selectedSession.getPatientId(), "-");
                String programName = programNameMap.getOrDefault(selectedSession.getProgramId(), "-");
                BigDecimal upfront = selectedSession.getUpfrontPayment() != null ? selectedSession.getUpfrontPayment() : BigDecimal.ZERO;

                lblPatient.setText(patientName);
                lblProgram.setText(programName);
                lblUpfront.setText(upfront.setScale(2).toString());

                BigDecimal progFee = programFeeMap.getOrDefault(selectedSession.getProgramId(), BigDecimal.ZERO);
                BigDecimal remaining = progFee.subtract(upfront);
                if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                    remaining = BigDecimal.ZERO;
                }
                amountField.setText(remaining.toString());
            } else {
                lblPatient.setText("-");
                lblProgram.setText("-");
                lblUpfront.setText("0.00");
                amountField.setText("");
            }
        });

        dialog.getDialogPane().setContent(grid);

        Button btnRecord = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        btnRecord.addEventFilter(ActionEvent.ACTION, eventFilter -> {
            if (sessionCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a Booked Session!").showAndWait();
                eventFilter.consume();
                return;
            }
            if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter an Amount!").showAndWait();
                eventFilter.consume();
                return;
            }
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    new Alert(Alert.AlertType.WARNING, "Amount cannot be negative!").showAndWait();
                    eventFilter.consume();
                    return;
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid Amount!").showAndWait();
                eventFilter.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                TherapySessionDTO selectedSession = sessionCombo.getValue();
                if (selectedSession == null) {
                    return null;
                }
                BigDecimal enteredAmount;
                try {
                    enteredAmount = new BigDecimal(amountField.getText().trim());
                } catch (NumberFormatException ex) {
                    enteredAmount = BigDecimal.ZERO;
                }

                BigDecimal upfront = selectedSession.getUpfrontPayment() != null ? selectedSession.getUpfrontPayment() : BigDecimal.ZERO;
                BigDecimal totalAmount = upfront.add(enteredAmount);

                PaymentDTo dto = new PaymentDTo();
                dto.setId(selectedSession.getPatientId());
                dto.setAmount(totalAmount);
                dto.setPaymentDate(LocalDateTime.now());
                dto.setPaymentMethod(methodCombo.getValue());
                dto.setPaymentStatus(statusCombo.getValue());
                dto.setInvoiceNumber(invoiceField.getText().trim());
                dto.setRemark(remarkField.getText().trim() + " (Session #" + selectedSession.getSessionId() + " Remaining Payment)");

                if (selectedSession.getPaymentId() != null) {
                    dto.setPaymentId(selectedSession.getPaymentId());
                }
                return dto;
            }
            return null;
        });

        Optional<PaymentDTo> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                boolean success;
                if (dto.getPaymentId() != null) {
                    success = paymentBO.updatePayment(dto);
                } else {
                    success = paymentBO.savePayment(dto);
                }
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Payment recorded successfully.").show();
                    loadMapsAndPayments();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to record payment.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error recording payment: " + e.getMessage()).show();
            }
        });
    }

    @FXML
    void handleViewInvoice(ActionEvent event) {
        PaymentDTo selected = tblPayments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a payment from the table to view invoice.").show();
            return;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("invoiceNumber", selected.getInvoiceNumber());
            params.put("paymentDate", selected.getPaymentDate() != null
                    ? selected.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : "N/A");
            params.put("patientName", patientMap.getOrDefault(selected.getId(), "Patient ID: " + selected.getId()));
            params.put("paymentMethod", selected.getPaymentMethod() != null ? selected.getPaymentMethod().name() : "N/A");
            params.put("paymentStatus", selected.getPaymentStatus() != null ? selected.getPaymentStatus().name() : "N/A");
            params.put("amount", selected.getAmount() != null ? "Rs. " + selected.getAmount().setScale(2).toString() : "Rs. 0.00");
            params.put("remark", selected.getRemark() != null ? selected.getRemark() : "N/A");

            ReportUtil.showReport("/reports/invoice.jrxml", params, null);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate Jasper invoice: " + e.getMessage()).show();
        }
    }
}
