package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.util.NavigateUtil;
import lk.ijse.the_serenity_mental_health_therapy_center.util.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

public class ReceptDashboardController implements Initializable {

    @FXML private AnchorPane mainPane;
    @FXML private AnchorPane bodyPane;
    @FXML private Button btnPatients;
    @FXML private Button btnSessions;
    @FXML private Button btnPayments;
    @FXML private Button btnReports;
    @FXML private ImageView profileDropdownBtn;

    private final NavigateUtil navigate = new NavigateUtil();

    private void selectTab(Button selectedBtn) {
        Button[] buttons = {btnPatients, btnSessions, btnPayments, btnReports};
        for (Button btn : buttons) {
            if (btn == null) continue;
            if (btn == selectedBtn) {
                btn.setStyle("-fx-background-color: #2C7A7B; -fx-text-fill: white; -fx-border-color: #2c7a7b;");
            } else {
                btn.setStyle("-fx-background-color: #FFF6DE; -fx-text-fill: #2c7a7b; -fx-border-color: #2c7a7b;");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectTab(btnPatients);
        navigate.navigateTo(bodyPane, "/view/PatientMgt.fxml");
    }

    @FXML
    void handleDashboardPage(ActionEvent event) {
        selectTab(btnPatients);
        navigate.navigateTo(bodyPane, "/view/PatientMgt.fxml");
    }

    @FXML
    void handlePatientsPage(ActionEvent event) {
        selectTab(btnPatients);
        navigate.navigateTo(bodyPane, "/view/PatientMgt.fxml");
    }

    @FXML
    void handleSessionsPage(ActionEvent event) {
        selectTab(btnSessions);
        navigate.navigateTo(bodyPane, "/view/SessionMgt.fxml");
    }

    @FXML
    void handlePaymentsPage(ActionEvent event) {
        selectTab(btnPayments);
        navigate.navigateTo(bodyPane, "/view/PaymentMgt.fxml");
    }

    @FXML
    void handleReportsPage(ActionEvent event) {
        selectTab(btnReports);
        navigate.navigateTo(bodyPane, "/view/ReceptReportMgt.fxml");
    }

    @FXML
    void handleProfileDropdown(MouseEvent event) {
        UserDTO currentUser = UserSession.getInstance().getLoggedInUser();
        String username = currentUser != null ? currentUser.getUsername() : "receptionist";

        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem usernameItem = new MenuItem("Logged in: " + username);
        usernameItem.setDisable(true);
        
        MenuItem profileItem = new MenuItem("Profile & Credentials Management");
        profileItem.setOnAction(e -> navigate.navigateTo(bodyPane, "/view/ProfileMgt.fxml"));
        
        MenuItem logoutItem = new MenuItem("Log Out");
        logoutItem.setOnAction(e -> {
            UserSession.getInstance().cleanUserSession();
            navigate.navigateBack(mainPane, "/view/Login.fxml");
        });
        
        contextMenu.getItems().addAll(usernameItem, new SeparatorMenuItem(), profileItem, logoutItem);
        contextMenu.show(profileDropdownBtn, Side.BOTTOM, 0, 0);
    }
}