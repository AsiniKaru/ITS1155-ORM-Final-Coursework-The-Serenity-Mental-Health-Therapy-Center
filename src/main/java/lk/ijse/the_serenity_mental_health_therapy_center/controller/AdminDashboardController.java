package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.util.NavigateUtil;
import lk.ijse.the_serenity_mental_health_therapy_center.util.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

        @FXML
        private AnchorPane mainPane;

        @FXML
        private AnchorPane bodyPane;
        @FXML
        private Button btnAdminReport;

        @FXML
        private Button btnPageHistory;

        @FXML
        private Button btnTherapist;

        @FXML
        private Button btnTherapistPrograme;

        @FXML
        private ImageView profileDropdownBtn;

        private final NavigateUtil navigate = new NavigateUtil();

        @FXML
        void handleProfileDropdown(MouseEvent event) {
            UserDTO loggedInUser = UserSession.getInstance().getLoggedInUser();
            String username = loggedInUser != null ? loggedInUser.getUsername() : "admin";

            ContextMenu contextMenu = new ContextMenu();

            MenuItem usernameItem = new MenuItem("Username: " + username);
            usernameItem.setDisable(true);
            usernameItem.setStyle("-fx-font-weight: bold; -fx-opacity: 1.0; -fx-text-fill: #2C7A7B;");

            MenuItem profileItem = new MenuItem("Profile & Credentials Management");
            profileItem.setOnAction(e -> navigate.navigateTo(bodyPane, "/view/ProfileMgt.fxml"));

            MenuItem manageUsersItem = new MenuItem("Manage Users");
            manageUsersItem.setOnAction(e -> navigate.navigateTo(bodyPane, "/view/UserMgt.fxml"));

            MenuItem logoutItem = new MenuItem("Log Out");
            logoutItem.setOnAction(e -> {
                UserSession.getInstance().cleanUserSession();
                navigate.navigateBack(mainPane, "/view/Login.fxml");
            });

            contextMenu.getItems().addAll(usernameItem, profileItem, manageUsersItem, logoutItem);
            contextMenu.show(profileDropdownBtn, Side.BOTTOM, 0, 0);
        }

        private void selectTab(Button selectedBtn) {
            Button[] buttons = {btnTherapist, btnTherapistPrograme, btnPageHistory, btnAdminReport};
            for (Button btn : buttons) {
                if (btn == selectedBtn) {
                    btn.setStyle("-fx-background-color: #2C7A7B; -fx-text-fill: white; -fx-border-color: #2c7a7b;");
                } else {
                    btn.setStyle("-fx-background-color: #FFF6DE; -fx-text-fill: #2c7a7b; -fx-border-color: #2c7a7b;");
                }
            }
        }

        @FXML
        void handleAdminReportPage(ActionEvent event) {
            selectTab(btnAdminReport);
            navigate.navigateTo(bodyPane, "/view/AdminReportMgt.fxml");
        }

        @FXML
        void handleTherapistPage(ActionEvent event) {
            selectTab(btnTherapist);
            navigate.navigateTo(bodyPane, "/view/TherapistMgt.fxml");
        }

        @FXML
        void handleTherapistProgramePage(ActionEvent event) {
            selectTab(btnTherapistPrograme);
            navigate.navigateTo(bodyPane, "/view/TherapyProgramMgt.fxml");
        }

        @FXML
        void handlePatientHistoryPage(ActionEvent event) {
            selectTab(btnPageHistory);
            navigate.navigateTo(bodyPane, "/view/PatientTherapyHistory.fxml");
        }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectTab(btnTherapist);
        navigate.navigateTo(bodyPane, "/view/TherapistMgt.fxml");
    }
}
