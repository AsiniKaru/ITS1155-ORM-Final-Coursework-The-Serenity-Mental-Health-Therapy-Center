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
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.TherapistAvailability;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TherapistMgtController {

    @FXML private TextField txtSearch;
    @FXML private TableView<TherapistDTO> tblTherapists;
    @FXML private TableColumn<TherapistDTO, String> colId;
    @FXML private TableColumn<TherapistDTO, String> colName;
    @FXML private TableColumn<TherapistDTO, String> colEmail;
    @FXML private TableColumn<TherapistDTO, String> colPhone;
    @FXML private TableColumn<TherapistDTO, String> colSpecialization;

    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    private final ObservableList<TherapistDTO> therapistList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind Table Columns
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTherapistId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTherapistFirstName() + " " + cellData.getValue().getTherapistLastName()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colSpecialization.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialization()));

        tblTherapists.setItems(therapistList);
        loadAllTherapists();
    }

    private void loadAllTherapists() {
        therapistList.clear();
        try {
            List<TherapistDTO> all = therapistBO.getAllTherapists();
            therapistList.addAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load therapists: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            loadAllTherapists();
            return;
        }
        query = query.trim().toLowerCase();

        String finalQuery = query;
        List<TherapistDTO> filtered = therapistList.stream()
                .filter(t -> t.getTherapistId().equals(finalQuery) ||
                        t.getTherapistFirstName().toLowerCase().contains(finalQuery) ||
                        t.getTherapistLastName().toLowerCase().contains(finalQuery) ||
                        t.getSpecialization().toLowerCase().contains(finalQuery))
                .collect(Collectors.toList());

        therapistList.setAll(filtered);
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadAllTherapists();
    }

    @FXML
    void handleAddTherapist(ActionEvent event) {
        showTherapistForm(null);
    }

    @FXML
    void handleEditTherapist(ActionEvent event) {
        TherapistDTO selected = tblTherapists.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapist to edit.").show();
            return;
        }
        showTherapistForm(selected);
    }

    @FXML
    void handleDeleteTherapist(ActionEvent event) {
        TherapistDTO selected = tblTherapists.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapist to delete.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove therapist " + selected.getTherapistFirstName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean success = therapistBO.deleteTherapist(selected.getTherapistId());
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Therapist deleted successfully.").show();
                    loadAllTherapists();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete therapist.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void handleAssignProgram(ActionEvent event) {
        TherapistDTO selected = tblTherapists.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapist from the table to assign a program.").show();
            return;
        }

        List<TherapyProgramDTO> programs;
        try {
            programs = programBO.getAllTherapyPrograms();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load therapy programs: " + e.getMessage()).show();
            return;
        }

        if (programs.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No therapy programs available. Please create a program first.").show();
            return;
        }

        Dialog<TherapyProgramDTO> dialog = new Dialog<>();
        dialog.setTitle("Assign Therapy Program");
        dialog.setHeaderText("Assign a program to Therapist: " + selected.getTherapistFirstName() + " " + selected.getTherapistLastName());

        ButtonType saveButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<TherapyProgramDTO> programCombo = new ComboBox<>();
        programCombo.setPromptText("Select Program");
        programCombo.getItems().addAll(programs);
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

        grid.add(new Label("Select Program:"), 0, 0);
        grid.add(programCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return programCombo.getValue();
            }
            return null;
        });

        Optional<TherapyProgramDTO> result = dialog.showAndWait();
        result.ifPresent(programDto -> {
            try {
                boolean success = therapistBO.assignProgramToTherapist(selected.getTherapistId(), programDto.getProgramId());
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Program assigned successfully.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to assign program.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        });
    }

    private void showTherapistForm(TherapistDTO existing) {
        Dialog<TherapistDTO> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Therapist" : "Edit Therapist");
        dialog.setHeaderText(existing == null ? "Enter Therapist Details" : "Modify Therapist Details");

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
        TextField specialization = new TextField();
        ComboBox<TherapistAvailability> availabilityCombo = new ComboBox<>();
        availabilityCombo.getItems().addAll(TherapistAvailability.values());
        availabilityCombo.setValue(TherapistAvailability.ACTIVE);

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phone, 1, 3);
        grid.add(new Label("Specialization:"), 0, 4);
        grid.add(specialization, 1, 4);
        grid.add(new Label("Availability:"), 0, 5);
        grid.add(availabilityCombo, 1, 5);

        if (existing != null) {
            firstName.setText(existing.getTherapistFirstName());
            lastName.setText(existing.getTherapistLastName());
            email.setText(existing.getEmail());
            phone.setText(existing.getPhone());
            specialization.setText(existing.getSpecialization());
            availabilityCombo.setValue(existing.getAvailability());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                TherapistDTO dto = new TherapistDTO();
                if (existing != null) {
                    dto.setTherapistId(existing.getTherapistId());
                }
                dto.setTherapistFirstName(firstName.getText());
                dto.setTherapistLastName(lastName.getText());
                dto.setEmail(email.getText());
                dto.setPhone(phone.getText());
                dto.setSpecialization(specialization.getText());
                dto.setAvailability(availabilityCombo.getValue());
                return dto;
            }
            return null;
        });

        Optional<TherapistDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                boolean success;
                if (existing == null) {
                    success = therapistBO.saveTherapist(dto);
                } else {
                    success = therapistBO.updateTherapist(dto);
                }
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Therapist saved successfully.").show();
                    loadAllTherapists();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save therapist details.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Validation/Database Error: " + e.getMessage()).show();
            }
        });
    }
}
