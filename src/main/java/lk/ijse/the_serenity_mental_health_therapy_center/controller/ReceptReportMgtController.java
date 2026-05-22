package lk.ijse.the_serenity_mental_health_therapy_center.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lk.ijse.the_serenity_mental_health_therapy_center.util.NavigateUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ReceptReportMgtController implements Initializable {

    @FXML
    private Button btnRevenue;

    @FXML
    private Button btnTxnHistory;

    @FXML
    private AnchorPane reportArea;

    private final NavigateUtil navigate = new NavigateUtil();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load default report sub-view
        navigate.navigateTo(reportArea, "/view/RevenueReport.fxml");
    }

    @FXML
    void handleRevenueReport(ActionEvent event) {
        navigate.navigateTo(reportArea, "/view/RevenueReport.fxml");
    }

    @FXML
    void handleTxnHistoryReport(ActionEvent event) {
        navigate.navigateTo(reportArea, "/view/TxnHistoryReport.fxml");
    }
}
