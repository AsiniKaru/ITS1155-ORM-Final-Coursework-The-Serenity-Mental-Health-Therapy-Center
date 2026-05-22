package lk.ijse.the_serenity_mental_health_therapy_center.bo;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getInstance() {
        return (boFactory == null) ? (boFactory = new BOFactory()) : boFactory;
    }

    public enum BOType {
        PATIENT,
        PAYMENT,
        THERAPIST,
        THERAPY_PROGRAM,
        THERAPY_SESSION,
        USER
    }

    public SuperBO getBO(BOType boType) {
        switch (boType) {
            case PATIENT:
                return new PatientBOImpl();
            case PAYMENT:
                return new PaymentBOImpl();
            case THERAPIST:
                return new TherapistBOImpl();
            case THERAPY_PROGRAM:
                return new TherapyProgramBOImpl();
            case THERAPY_SESSION:
                return new TherapySessionBOImpl();
            case USER:
                return new UserBOImpl();
            default:
                return null;
        }
    }

}
