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
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TherapyProgramController {

    @FXML private TextField txtSearch;
    @FXML private TableView<TherapyProgramDTO> tblPrograms;
    @FXML private TableColumn<TherapyProgramDTO, String> colCode;
    @FXML private TableColumn<TherapyProgramDTO, String> colName;
    @FXML private TableColumn<TherapyProgramDTO, String> colDuration;
    @FXML private TableColumn<TherapyProgramDTO, String> colFee;
    @FXML private TableColumn<TherapyProgramDTO, String> colDescription;

    private final TherapyProgramBO programBO = (TherapyProgramBO) BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);
    private final ObservableList<TherapyProgramDTO> programList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind columns
        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProgramCode()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProgramName()));
        colDuration.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDuration()));
        colFee.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProgramFee() != null ? cellData.getValue().getProgramFee().toString() : "0.00"));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        tblPrograms.setItems(programList);
        loadAllPrograms();
    }

    private void loadAllPrograms() {
        programList.clear();
        try {
            List<TherapyProgramDTO> all = programBO.getAllTherapyPrograms();
            programList.addAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load therapy programs: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            loadAllPrograms();
            return;
        }
        query = query.trim().toLowerCase();

        String finalQuery = query;
        List<TherapyProgramDTO> filtered = programList.stream()
                .filter(p -> p.getProgramCode().toLowerCase().contains(finalQuery) ||
                        p.getProgramName().toLowerCase().contains(finalQuery))
                .collect(Collectors.toList());

        programList.setAll(filtered);
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadAllPrograms();
    }

    @FXML
    void handleAddProgram(ActionEvent event) {
        showProgramForm(null);
    }

    @FXML
    void handleEditProgram(ActionEvent event) {
        TherapyProgramDTO selected = tblPrograms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapy program to edit.").show();
            return;
        }
        showProgramForm(selected);
    }

    @FXML
    void handleDeleteProgram(ActionEvent event) {
        TherapyProgramDTO selected = tblPrograms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapy program to delete.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete program " + selected.getProgramName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean success = programBO.deleteTherapyProgram(selected.getProgramId());
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Program deleted successfully.").show();
                    loadAllPrograms();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete program.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
        }
    }

    private void showProgramForm(TherapyProgramDTO existing) {
        Dialog<TherapyProgramDTO> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Therapy Program" : "Edit Therapy Program");
        dialog.setHeaderText(existing == null ? "Enter Program Details" : "Modify Program Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField programCode = new TextField();
        TextField name = new TextField();
        TextField duration = new TextField();
        TextField fee = new TextField();
        TextArea description = new TextArea();
        description.setPrefRowCount(3);

        grid.add(new Label("Program Code:"), 0, 0);
        grid.add(programCode, 1, 0);
        grid.add(new Label("Program Name:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Duration (e.g. 8 Weeks):"), 0, 2);
        grid.add(duration, 1, 2);
        grid.add(new Label("Fee (LKR):"), 0, 3);
        grid.add(fee, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(description, 1, 4);

        if (existing != null) {
            programCode.setText(existing.getProgramCode());
            name.setText(existing.getProgramName());
            duration.setText(existing.getDuration());
            fee.setText(existing.getProgramFee() != null ? existing.getProgramFee().toString() : "");
            description.setText(existing.getDescription());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                TherapyProgramDTO dto = new TherapyProgramDTO();
                if (existing != null) {
                    dto.setProgramId(existing.getProgramId());
                }
                dto.setProgramCode(programCode.getText());
                dto.setProgramName(name.getText());
                dto.setDuration(duration.getText());
                try {
                    dto.setProgramFee(new BigDecimal(fee.getText().trim()));
                } catch (NumberFormatException e) {
                    dto.setProgramFee(BigDecimal.ZERO);
                }
                dto.setDescription(description.getText());
                return dto;
            }
            return null;
        });

        Optional<TherapyProgramDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            try {
                boolean success;
                if (existing == null) {
                    success = programBO.saveTherapyProgram(dto);
                } else {
                    success = programBO.updateTherapyProgram(dto);
                }
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "Therapy program saved successfully.").show();
                    loadAllPrograms();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save program details.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Validation/Database Error: " + e.getMessage()).show();
            }
        });
    }
}
