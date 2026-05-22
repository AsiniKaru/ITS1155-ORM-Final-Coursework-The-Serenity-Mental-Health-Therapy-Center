package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.util.UserSession;

public class ProfileMgtController {

    @FXML private Label lblCurrentUsername;
    @FXML private TextField txtNewUsername;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmNewPassword;
    @FXML private PasswordField txtCurrentPassword;
    @FXML private Label lblMessage;
    @FXML private Button btnUpdate;
    @FXML private Button btnCancel;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @FXML
    public void initialize() {
        lblMessage.setText("");
        UserDTO loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            lblCurrentUsername.setText(loggedInUser.getUsername());
        }
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        lblMessage.setText("");

        UserDTO loggedInUser = UserSession.getInstance().getLoggedInUser();
        if (loggedInUser == null) {
            lblMessage.setText("Error: No user session active.");
            return;
        }

        String currentPassword = txtCurrentPassword.getText();
        if (currentPassword == null || currentPassword.isBlank()) {
            lblMessage.setText("Current password is required to verify changes.");
            return;
        }

        // Verify current credentials
        try {
            userBO.login(loggedInUser.getUsername(), currentPassword);
        } catch (Exception e) {
            lblMessage.setText("Current password verification failed: " + e.getMessage());
            return;
        }

        String newUsername = txtNewUsername.getText();
        if (newUsername != null) {
            newUsername = newUsername.trim();
        }
        if (newUsername == null || newUsername.isEmpty()) {
            newUsername = loggedInUser.getUsername();
        }

        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmNewPassword.getText();

        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                lblMessage.setText("New passwords do not match.");
                return;
            }
        }

        try {
            // Update credentials
            UserDTO updatedDto = new UserDTO();
            updatedDto.setId(loggedInUser.getId());
            updatedDto.setUsername(newUsername);
            updatedDto.setPassword(newPassword);
            updatedDto.setFirstName(loggedInUser.getFirstName());
            updatedDto.setLastName(loggedInUser.getLastName());
            updatedDto.setEmail(loggedInUser.getEmail());
            updatedDto.setRole(loggedInUser.getRole());
            updatedDto.setActive(loggedInUser.isActive());

            boolean success = userBO.updateUser(updatedDto);
            if (success) {
                // Update local session
                loggedInUser.setUsername(newUsername);
                lblCurrentUsername.setText(newUsername);
                
                txtNewUsername.clear();
                txtNewPassword.clear();
                txtConfirmNewPassword.clear();
                txtCurrentPassword.clear();
                
                new Alert(Alert.AlertType.INFORMATION, "Profile updated successfully!").show();
            } else {
                lblMessage.setText("Failed to update profile.");
            }
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        txtNewUsername.clear();
        txtNewPassword.clear();
        txtConfirmNewPassword.clear();
        txtCurrentPassword.clear();
        lblMessage.setText("");
    }
}
