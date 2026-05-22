package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapySessionBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapistDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapySessionDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientTherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.SessionStatus;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Patient;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.PatientTherapyProgram;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapyProgram;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapySession;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TherapySessionBOImpl implements TherapySessionBO {

    private final TherapySessionDAO sessionDAO = (TherapySessionDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_SESSION);
    private final PatientDAO patientDAO = (PatientDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);
    private final TherapistDAO therapistDAO = (TherapistDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPIST);
    private final TherapyProgramDAO programDAO = (TherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
    private final PatientTherapyProgramDAO patientProgramDAO = (PatientTherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT_THERAPY_PROGRAM);

    @Override
    public boolean saveSession(TherapySessionDTO dto) {
        if (dto.getPatientId() == null || dto.getTherapistId() == null || dto.getProgramId() == null) {
            throw new RuntimeException("Patient, Therapist, and Program must be selected!");
        }
        if (dto.getSessionDate() == null || dto.getSessionTime() == null) {
            throw new RuntimeException("Session Date and Time must be set!");
        }

        // Fetch patient with eager-loaded programs to avoid LazyInitializationException
        List<Patient> patients = patientDAO.getPatientsWithPrograms();
        Patient patient = null;
        for (Patient p : patients) {
            if (p.getPatientId().toString().equals(dto.getPatientId())) {
                patient = p;
                break;
            }
        }

        if (patient == null) {
            throw new RuntimeException("Patient not found!");
        }

        TherapyProgram program = programDAO.search(dto.getProgramId());
        if (program == null) {
            throw new RuntimeException("Therapy Program not found!");
        }

        // Find the enrollment program
        PatientTherapyProgram enrollment = null;
        for (PatientTherapyProgram pt : patient.getPatientTherapyPrograms()) {
            if (pt.getProgram().getProgramId().toString().equals(dto.getProgramId())) {
                enrollment = pt;
                break;
            }
        }

        if (enrollment == null) {
            enrollment = new PatientTherapyProgram(patient, program, 1);
            patientProgramDAO.save(enrollment);
        } else if (enrollment.getRemainingCredit() <= 0) {
            enrollment.setUpfrontSessionsPaid(enrollment.getUpfrontSessionsPaid() + 1);
            patientProgramDAO.update(enrollment);
        }

        Therapist therapist = therapistDAO.search(dto.getTherapistId());
        if (therapist == null) {
            throw new RuntimeException("Therapist not found!");
        }

        // Increment sessions used and update enrollment
        enrollment.setSessionsUsed(enrollment.getSessionsUsed() + 1);
        patientProgramDAO.update(enrollment);

        // Save session
        TherapySession session = new TherapySession();
        session.setPatient(patient);
        session.setTherapist(therapist);
        session.setTherapyProgram(program);
        session.setSessionDate(dto.getSessionDate());
        session.setSessionTime(dto.getSessionTime());
        session.setStatus(dto.getSessionStatus() != null ? 
                TherapySession.SessionStatus.valueOf(dto.getSessionStatus().name()) : 
                TherapySession.SessionStatus.SCHEDULED);
        session.setNotes(dto.getNote());

        BigDecimal upfront = dto.getUpfrontPayment() != null ? dto.getUpfrontPayment() : BigDecimal.ZERO;
        session.setUpfrontAmount(upfront);

        Payment payment = new Payment();
        payment.setPatient(patient);
        payment.setAmount(upfront);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setMethod(Payment.Method.CASH);
        payment.setStatus(Payment.PaymentStatus.UPFRONT);
        payment.setInvoiceNumber("INV-UP-" + System.currentTimeMillis() / 1000);
        payment.setRemarks("Upfront payment for Session booking");

        session.setPayment(payment);

        return sessionDAO.save(session);
    }

    @Override
    public boolean updateSession(TherapySessionDTO dto) {
        TherapySession session = sessionDAO.search(dto.getSessionId());
        if (session == null) return false;

        Patient patient = patientDAO.search(dto.getPatientId());
        Therapist therapist = therapistDAO.search(dto.getTherapistId());
        TherapyProgram program = programDAO.search(dto.getProgramId());

        if (patient == null || therapist == null || program == null) {
            throw new RuntimeException("Patient, Therapist, or Program not found!");
        }

        session.setPatient(patient);
        session.setTherapist(therapist);
        session.setTherapyProgram(program);
        session.setSessionDate(dto.getSessionDate());
        session.setSessionTime(dto.getSessionTime());
        if (dto.getSessionStatus() != null) {
            TherapySession.SessionStatus oldStatus = session.getStatus();
            TherapySession.SessionStatus newStatus = TherapySession.SessionStatus.valueOf(dto.getSessionStatus().name());
            if (oldStatus == TherapySession.SessionStatus.SCHEDULED && newStatus == TherapySession.SessionStatus.CANCELLED) {
                // Refund credit
                List<Patient> patients = patientDAO.getPatientsWithPrograms();
                Patient p = null;
                for (Patient pat : patients) {
                    if (pat.getPatientId().equals(session.getPatient().getPatientId())) {
                        p = pat;
                        break;
                    }
                }
                if (p != null) {
                    for (PatientTherapyProgram pt : p.getPatientTherapyPrograms()) {
                        if (pt.getProgram().getProgramId().equals(session.getTherapyProgram().getProgramId())) {
                            pt.setSessionsUsed(Math.max(0, pt.getSessionsUsed() - 1));
                            patientProgramDAO.update(pt);
                            break;
                        }
                    }
                }
            }
            session.setStatus(newStatus);
        }
        session.setNotes(dto.getNote());

        return sessionDAO.update(session);
    }

    @Override
    public boolean deleteSession(String id) {
        return sessionDAO.delete(id);
    }

    @Override
    public TherapySessionDTO searchSession(String id) {
        TherapySession session = sessionDAO.search(id);
        if (session == null) return null;
        return toDTO(session);
    }

    @Override
    public List<TherapySessionDTO> getAllSessions() {
        List<TherapySession> list = sessionDAO.getAll();
        List<TherapySessionDTO> dtoList = new ArrayList<>();
        for (TherapySession session : list) {
            dtoList.add(toDTO(session));
        }
        return dtoList;
    }

    private TherapySessionDTO toDTO(TherapySession session) {
        return new TherapySessionDTO(
                session.getSessionId().toString(),
                session.getPatient().getPatientId().toString(),
                session.getTherapist().getTherapistId().toString(),
                session.getTherapyProgram().getProgramId().toString(),
                session.getSessionDate(),
                session.getSessionTime(),
                SessionStatus.valueOf(session.getStatus().name()),
                session.getUpfrontAmount(),
                session.getNotes(),
                session.getPayment() != null ? session.getPayment().getPaymentId().toString() : null,
                session.getPayment() != null ? session.getPayment().getStatus().name() : null
        );
    }
}
