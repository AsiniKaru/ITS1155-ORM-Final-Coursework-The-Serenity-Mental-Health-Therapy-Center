package lk.ijse.the_serenity_mental_health_therapy_center;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("view/PatientMgt.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 500);
        stage.setTitle("Serenity Mental Health Therapy Center");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}