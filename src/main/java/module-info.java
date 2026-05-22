module lk.ijse.the_serenity_mental_health_therapy_center {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires jbcrypt;
    requires java.naming;
    requires java.sql;
    requires java.desktop;
    requires jasperreports;


    opens lk.ijse.the_serenity_mental_health_therapy_center to javafx.fxml;
    opens lk.ijse.the_serenity_mental_health_therapy_center.entity to org.hibernate.orm.core;
    opens lk.ijse.the_serenity_mental_health_therapy_center.dto to jasperreports;
    exports lk.ijse.the_serenity_mental_health_therapy_center;
    exports lk.ijse.the_serenity_mental_health_therapy_center.controller;
    opens lk.ijse.the_serenity_mental_health_therapy_center.controller to javafx.fxml, jasperreports;
}