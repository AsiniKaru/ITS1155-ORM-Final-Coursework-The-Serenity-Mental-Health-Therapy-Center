package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lk.ijse.the_serenity_mental_health_therapy_center.util.NavigateUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminReportController implements Initializable {

    @FXML
    private Button btnTherapistPerf;

    @FXML
    private Button btnTherapySession;

    @FXML
    private AnchorPane reportArea;

    private final NavigateUtil navigate = new NavigateUtil();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load default report sub-view
        navigate.navigateTo(reportArea, "/view/TherapistPerformanceReport.fxml");
    }

    @FXML
    void handleTherapistPerformance(ActionEvent event) {
        navigate.navigateTo(reportArea, "/view/TherapistPerformanceReport.fxml");
    }

    @FXML
    void handleTherapySession(ActionEvent event) {
        navigate.navigateTo(reportArea, "/view/AdminSessionReport.fxml");
    }
}
