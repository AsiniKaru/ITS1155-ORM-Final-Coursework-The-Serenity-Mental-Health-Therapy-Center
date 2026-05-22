package lk.ijse.the_serenity_mental_health_therapy_center.config;

import lk.ijse.the_serenity_mental_health_therapy_center.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() {
        Configuration configuration = new Configuration();
        
        // Register entities
        configuration.addAnnotatedClass(User.class)
                     .addAnnotatedClass(Patient.class)
                     .addAnnotatedClass(Therapist.class)
                     .addAnnotatedClass(TherapyProgram.class)
                     .addAnnotatedClass(PatientTherapyProgram.class)
                     .addAnnotatedClass(TherapySession.class)
                     .addAnnotatedClass(Payment.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    public static FactoryConfiguration getInstance() {
        return (factoryConfiguration == null) ? (factoryConfiguration = new FactoryConfiguration()) : factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}
