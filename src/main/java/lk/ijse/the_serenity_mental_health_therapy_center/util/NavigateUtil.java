package lk.ijse.the_serenity_mental_health_therapy_center.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigateUtil {

    public void navigateTo(Pane basePane, String fxmlPath) {
        try {
            basePane.getChildren().clear();
            String fullPath = fxmlPath;
            if (fxmlPath.startsWith("/view/")) {
                fullPath = "/lk/ijse/the_serenity_mental_health_therapy_center" + fxmlPath;
            }
            AnchorPane load = FXMLLoader.load(getClass().getResource(fullPath));

            load.prefWidthProperty().bind(basePane.widthProperty());
            load.prefHeightProperty().bind(basePane.heightProperty());

            basePane.getChildren().add(load);
        } catch (Throwable e) {
            System.err.println("Error loading page: " + fxmlPath);
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load page: " + fxmlPath + "\nError: " + e.getMessage() + "\nCause: " + e.getCause()).show();
        }
    }

    public void navigateBack(AnchorPane currentPane, String path) {
        try {
            String fullPath = path;
            if (path.startsWith("/view/")) {
                fullPath = "/lk/ijse/the_serenity_mental_health_therapy_center" + path;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
            AnchorPane newPane = loader.load();

            Stage stage = (Stage) currentPane.getScene().getWindow();
            Scene scene = new Scene(newPane);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Throwable e) {
            System.err.println("Error navigating back to: " + path);
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to navigate back to: " + path + "\nError: " + e.getMessage() + "\nCause: " + e.getCause()).show();
        }
    }

}