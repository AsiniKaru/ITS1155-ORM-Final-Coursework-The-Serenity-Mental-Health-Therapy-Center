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
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.UserRole;
import lk.ijse.the_serenity_mental_health_therapy_center.util.UserSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserMgtController {

    @FXML private TextField txtSearch;
    @FXML private TableView<UserDTO> tblUsers;
    @FXML private TableColumn<UserDTO, String> colId;
    @FXML private TableColumn<UserDTO, String> colUsername;
    @FXML private TableColumn<UserDTO, String> colName;
    @FXML private TableColumn<UserDTO, String> colEmail;
    @FXML private TableColumn<UserDTO, String> colRole;
    @FXML private TableColumn<UserDTO, String> colStatus;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOType.USER);
    private final ObservableList<UserDTO> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind columns
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().name()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isActive() ? "ACTIVE" : "INACTIVE"));

        tblUsers.setItems(userList);
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        try {
            List<UserDTO> list = userBO.getAllUsers();
            userList.addAll(list);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load users: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            tblUsers.setItems(userList);
            return;
        }
        ObservableList<UserDTO> filtered = userList.stream().filter(u ->
            String.valueOf(u.getId()).contains(query) ||
            (u.getUsername() != null && u.getUsername().toLowerCase().contains(query)) ||
            (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(query)) ||
            (u.getLastName() != null && u.getLastName().toLowerCase().contains(query)) ||
            (u.getEmail() != null && u.getEmail().toLowerCase().contains(query))
        ).collect(Collectors.toCollection(FXCollections::observableArrayList));
        tblUsers.setItems(filtered);
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadUsers();
    }

    @FXML
    void handleAddUser(ActionEvent event) {
        Dialog<UserDTO> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new Admin or Receptionist account");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        ComboBox<UserRole> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(UserRole.values());
        roleCombo.setValue(UserRole.RECEPTIONIST);

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Role:"), 0, 5);
        grid.add(roleCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                UserDTO dto = new UserDTO();
                dto.setUsername(usernameField.getText().trim());
                dto.setPassword(passwordField.getText().trim());
                dto.setFirstName(firstNameField.getText().trim());
                dto.setLastName(lastNameField.getText().trim());
                dto.setEmail(emailField.getText().trim());
                dto.setRole(roleCombo.getValue());
                dto.setActive(true);
                return dto;
            }
            return null;
        });

        Optional<UserDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            if (dto.getUsername().isEmpty() || dto.getPassword().isEmpty() || dto.getFirstName().isEmpty() || dto.getLastName().isEmpty() || dto.getEmail().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "All fields are required!").show();
                return;
            }
            try {
                boolean success = userBO.registerUser(dto);
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "User registered successfully.").show();
                    loadUsers();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to register user.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error saving user: " + e.getMessage()).show();
            }
        });
    }

    @FXML
    void handleEditUser(ActionEvent event) {
        UserDTO selectedUser = tblUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to edit.").show();
            return;
        }

        Dialog<UserDTO> dialog = new Dialog<>();
        dialog.setTitle("Edit User Details");
        dialog.setHeaderText("Modify User Details");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField(selectedUser.getUsername());
        TextField firstNameField = new TextField(selectedUser.getFirstName());
        TextField lastNameField = new TextField(selectedUser.getLastName());
        TextField emailField = new TextField(selectedUser.getEmail());

        ComboBox<UserRole> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(UserRole.values());
        roleCombo.setValue(selectedUser.getRole());

        CheckBox activeCheck = new CheckBox("Active");
        activeCheck.setSelected(selectedUser.isActive());

        UserDTO loggedIn = UserSession.getInstance().getLoggedInUser();
        if (loggedIn != null && loggedIn.getId() == selectedUser.getId()) {
            activeCheck.setDisable(true);
        }

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(activeCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                UserDTO dto = new UserDTO();
                dto.setId(selectedUser.getId());
                dto.setUsername(usernameField.getText().trim());
                dto.setPassword(null);
                dto.setFirstName(firstNameField.getText().trim());
                dto.setLastName(lastNameField.getText().trim());
                dto.setEmail(emailField.getText().trim());
                dto.setRole(roleCombo.getValue());
                dto.setActive(activeCheck.isSelected());
                return dto;
            }
            return null;
        });

        Optional<UserDTO> result = dialog.showAndWait();
        result.ifPresent(dto -> {
            if (dto.getUsername().isEmpty() || dto.getFirstName().isEmpty() || dto.getLastName().isEmpty() || dto.getEmail().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "All fields are required!").show();
                return;
            }
            try {
                boolean success = userBO.updateUser(dto);
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "User updated successfully.").show();
                    loadUsers();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update user.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error updating user: " + e.getMessage()).show();
            }
        });
    }

    @FXML
    void handleToggleStatus(ActionEvent event) {
        UserDTO selected = tblUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to activate/deactivate.").show();
            return;
        }

        UserDTO loggedIn = UserSession.getInstance().getLoggedInUser();
        if (loggedIn != null && loggedIn.getId() == selected.getId()) {
            new Alert(Alert.AlertType.WARNING, "You cannot deactivate your own account!").show();
            return;
        }

        String action = selected.isActive() ? "deactivate" : "activate";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to " + action + " user " + selected.getUsername() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                selected.setActive(!selected.isActive());
                boolean success = userBO.updateUser(selected);
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION, "User status updated successfully.").show();
                    loadUsers();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update user status.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error updating status: " + e.getMessage()).show();
            }
        }
    }
}