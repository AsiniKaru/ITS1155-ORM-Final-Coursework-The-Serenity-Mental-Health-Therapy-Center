package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.UserRole;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.User;
import lk.ijse.the_serenity_mental_health_therapy_center.exception.InvalidCredentialsException;
import lk.ijse.the_serenity_mental_health_therapy_center.util.PasswordUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Button btnLogin;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox showPassword;
    @FXML private Label errorMsg;
    @FXML private Text txtForgetPassword;
    @FXML private TextField txtUsername;
    @FXML private TextField passwordTxt;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordTxt.setVisible(false);
        passwordTxt.setManaged(false);
        errorMsg.setText("");
    }

    @FXML
    void checkboxPassword(ActionEvent event) {
        if (showPassword.isSelected()) {
            passwordTxt.setText(passwordField.getText());
            passwordTxt.setVisible(true);
            passwordTxt.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            showPassword.setText("Hide password");
        } else {
            passwordField.setText(passwordTxt.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTxt.setVisible(false);
            passwordTxt.setManaged(false);
            showPassword.setText("Show password");
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        try {
            checkEmptyFields();

            String username = txtUsername.getText();
            String password = showPassword.isSelected() ? passwordTxt.getText() : passwordField.getText();

            UserDTO userDto = userBO.login(username, password);
            UserRole role = userDto.getRole();
            
            // Save user to session
            lk.ijse.the_serenity_mental_health_therapy_center.util.UserSession.getInstance().setLoggedInUser(userDto);

            String fxmlPath = null;
            String title = null;
            if (role == UserRole.ADMIN) {
                fxmlPath = "/lk/ijse/the_serenity_mental_health_therapy_center/view/AdminDashboard.fxml";
                title = "Serenity Mental Health Therapy Center - Admin Dashboard";
            } else if (role == UserRole.RECEPTIONIST) {
                fxmlPath = "/lk/ijse/the_serenity_mental_health_therapy_center/view/ReceptDashboard.fxml";
                title = "Serenity Mental Health Therapy Center - Receptionist Dashboard";
            }

            if (fxmlPath == null) {
                errorMsg.setText("Unknown user role!");
                return;
            }

            final String path = fxmlPath;
            final String finalTitle = title;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), txtUsername.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                    Parent root = loader.load();
                    Stage stage = (Stage) txtUsername.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.sizeToScene();
                    stage.setResizable(false);
                    stage.setTitle(finalTitle);
                    stage.centerOnScreen();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    errorMsg.setText("Failed to load dashboard");
                }
            });
            fadeOut.play();

        } catch (InvalidCredentialsException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            new Alert(Alert.AlertType.ERROR, "An unexpected error occurred: " + e.getMessage()).show();
        }
    }



    private void checkEmptyFields() {
        if (txtUsername.getText() == null || txtUsername.getText().trim().isEmpty()) {
            errorMsg.setText("Username field cannot be empty!");
            throw new RuntimeException("Username field cannot be empty!");
        }
        String pass = showPassword.isSelected() ? passwordTxt.getText() : passwordField.getText();
        if (pass == null || pass.trim().isEmpty()) {
            errorMsg.setText("Password field cannot be empty!");
            throw new RuntimeException("Password field cannot be empty!");
        }
    }



}