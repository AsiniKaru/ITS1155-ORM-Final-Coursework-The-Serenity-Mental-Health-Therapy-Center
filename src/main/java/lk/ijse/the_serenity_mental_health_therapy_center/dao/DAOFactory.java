package lk.ijse.the_serenity_mental_health_therapy_center.dao;

import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl.*;

public class DAOFactory {

    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        return (daoFactory == null) ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    public enum DAOType {
        PATIENT,
        PAYMENT,
        THERAPIST,
        PATIENT_THERAPY_PROGRAM,
        THERAPY_PROGRAM,
        THERAPY_SESSION,
        USER
    }

    public SuperDAO getDAO(DAOType type) {
        switch (type) {
            case PATIENT:
                return new PatientDAOImpl();
            case PAYMENT:
                return new PaymentDAOImpl();
            case THERAPIST:
                return new TherapistDAOImpl();
            case PATIENT_THERAPY_PROGRAM:
                return new PatientTherapyProgramDAOImpl();
            case THERAPY_PROGRAM:
                return new TherapyProgramDAOImpl();
            case THERAPY_SESSION:
                return new TherapySessionDAOImpl();
            case USER:
                return new UserDAOImpl();
            default:
                return null;
        }
    }
}
