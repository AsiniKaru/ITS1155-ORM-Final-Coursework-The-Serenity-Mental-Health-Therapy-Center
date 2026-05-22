package lk.ijse.the_serenity_mental_health_therapy_center;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.UserDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.User;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapyProgram;
import javafx.scene.Parent;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Seed default users and programs if tables are empty
        try {
            seedDefaultUsers();
            seedDefaultPrograms();
        } catch (Exception e) {
            System.err.println("Error seeding default data: " + e.getMessage());
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("view/Login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Serenity Mental Health Therapy Center");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    private void seedDefaultUsers() {
        UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.USER);
        if (userDAO.getAll().isEmpty()) {
            System.out.println("No users found in database. Seeding default Admin and Receptionist...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash("$2a$12$kQ13c6XhbdRO15p.GUexguBuO2FfSIFlkCE0hEQ1ktCecWeScdxFa");
            admin.setFName("Admin");
            admin.setLName("User");
            admin.setEmail("admin@serenity.lk");
            admin.setRole(User.UserRole.ADMIN);
            admin.setActive(true);
            userDAO.save(admin);

            User receptionist = new User();
            receptionist.setUsername("receptionist");
            receptionist.setPasswordHash("$2a$12$sYQGcQlMEZUWO3bWyJVvp.Vr54TQG5AzwNIKEo7KxQJqFn73m0qiK");
            receptionist.setFName("Recept");
            receptionist.setLName("User");
            receptionist.setEmail("receptionist@serenity.lk");
            receptionist.setRole(User.UserRole.RECEPTIONIST);
            receptionist.setActive(true);
            userDAO.save(receptionist);
            
            System.out.println("Default users seeded successfully.");
        }
    }

    private void seedDefaultPrograms() {
        TherapyProgramDAO programDAO = (TherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
        if (programDAO.getAll().isEmpty()) {
            System.out.println("No therapy programs found in database. Seeding default programs...");

            TherapyProgram p1 = new TherapyProgram();
            p1.setProgramCode("MT1001");
            p1.setName("Cognitive Behavioral Therapy");
            p1.setDurationWeeks("12 weeks");
            p1.setFee(new java.math.BigDecimal("80000.00"));
            p1.setDescription("Cognitive Behavioral Therapy");
            programDAO.save(p1);

            TherapyProgram p2 = new TherapyProgram();
            p2.setProgramCode("MT1002");
            p2.setName("Mindfulness-Based Stress Reduction");
            p2.setDurationWeeks("8 weeks");
            p2.setFee(new java.math.BigDecimal("50000.00"));
            p2.setDescription("Mindfulness-Based Stress Reduction");
            programDAO.save(p2);

            TherapyProgram p3 = new TherapyProgram();
            p3.setProgramCode("MT1003");
            p3.setName("Dialectical Behavior Therapy");
            p3.setDurationWeeks("16 weeks");
            p3.setFee(new java.math.BigDecimal("100000.00"));
            p3.setDescription("Dialectical Behavior Therapy");
            programDAO.save(p3);

            TherapyProgram p4 = new TherapyProgram();
            p4.setProgramCode("MT1004");
            p4.setName("Group Therapy Sessions");
            p4.setDurationWeeks("6 months");
            p4.setFee(new java.math.BigDecimal("120000.00"));
            p4.setDescription("Group Therapy Sessions");
            programDAO.save(p4);

            TherapyProgram p5 = new TherapyProgram();
            p5.setProgramCode("MT1005");
            p5.setName("Family Counseling");
            p5.setDurationWeeks("3 months");
            p5.setFee(new java.math.BigDecimal("40000.00"));
            p5.setDescription("Family Counseling");
            programDAO.save(p5);

            System.out.println("Default therapy programs seeded successfully.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}