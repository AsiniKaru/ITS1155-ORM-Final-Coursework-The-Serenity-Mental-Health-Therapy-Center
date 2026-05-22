package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PaymentBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PaymentDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PaymentDTo;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.PaymentMethod;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.PaymentStatus;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Patient;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Payment;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.PatientTherapyProgram;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapyProgram;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientTherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentBOImpl implements PaymentBO {

    private final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PAYMENT);
    private final PatientDAO patientDAO = (PatientDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);
    private final TherapyProgramDAO programDAO = (TherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
    private final PatientTherapyProgramDAO patientProgramDAO = (PatientTherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT_THERAPY_PROGRAM);

    @Override
    public boolean savePayment(PaymentDTo dto) {
        if (dto.getAmount() == null) {
            throw new RuntimeException("Payment Amount cannot be empty!");
        }
        ValidationUtil.checkRequiredField(dto.getInvoiceNumber(), "Invoice Number");

        Patient patient = patientDAO.search(dto.getId());
        if (patient == null) {
            throw new RuntimeException("Patient not found!");
        }

        Payment payment = new Payment();
        payment.setPatient(patient);
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDateTime.now());
        payment.setMethod(Payment.Method.valueOf(dto.getPaymentMethod().name()));
        payment.setStatus(Payment.PaymentStatus.valueOf(dto.getPaymentStatus().name()));
        payment.setInvoiceNumber(dto.getInvoiceNumber());
        payment.setRemarks(dto.getRemark());

        boolean success = paymentDAO.save(payment);
        if (success && dto.getProgramId() != null && dto.getSessionsToBuy() != null && dto.getSessionsToBuy() > 0) {
            List<Patient> patients = patientDAO.getPatientsWithPrograms();
            Patient pWithPrograms = null;
            for (Patient p : patients) {
                if (p.getPatientId().toString().equals(dto.getId())) {
                    pWithPrograms = p;
                    break;
                }
            }
            if (pWithPrograms != null) {
                PatientTherapyProgram enrollment = null;
                for (PatientTherapyProgram pt : pWithPrograms.getPatientTherapyPrograms()) {
                    if (pt.getProgram().getProgramId().toString().equals(dto.getProgramId())) {
                        enrollment = pt;
                        break;
                    }
                }
                if (enrollment != null) {
                    // Update credits
                    enrollment.setUpfrontSessionsPaid(enrollment.getUpfrontSessionsPaid() + dto.getSessionsToBuy());
                    patientProgramDAO.update(enrollment);
                } else {
                    // Create new enrollment
                    TherapyProgram program = programDAO.search(dto.getProgramId());
                    if (program != null) {
                        PatientTherapyProgram newEnrollment = new PatientTherapyProgram(pWithPrograms, program, dto.getSessionsToBuy());
                        patientProgramDAO.save(newEnrollment);
                    }
                }
            }
        }
        return success;
    }

    @Override
    public boolean updatePayment(PaymentDTo dto) {
        if (dto.getAmount() == null) {
            throw new RuntimeException("Payment Amount cannot be empty!");
        }
        ValidationUtil.checkRequiredField(dto.getInvoiceNumber(), "Invoice Number");

        Payment payment = paymentDAO.search(dto.getPaymentId());
        if (payment == null) return false;

        Patient patient = patientDAO.search(dto.getId());
        if (patient == null) {
            throw new RuntimeException("Patient not found!");
        }

        payment.setPatient(patient);
        payment.setAmount(dto.getAmount());
        if (dto.getPaymentDate() != null) {
            payment.setPaymentDate(dto.getPaymentDate());
        }
        payment.setMethod(Payment.Method.valueOf(dto.getPaymentMethod().name()));
        payment.setStatus(Payment.PaymentStatus.valueOf(dto.getPaymentStatus().name()));
        payment.setInvoiceNumber(dto.getInvoiceNumber());
        payment.setRemarks(dto.getRemark());

        return paymentDAO.update(payment);
    }

    @Override
    public boolean deletePayment(String id) {
        return paymentDAO.delete(id);
    }

    @Override
    public PaymentDTo searchPayment(String id) {
        Payment payment = paymentDAO.search(id);
        if (payment == null) return null;
        return toDTO(payment);
    }

    @Override
    public List<PaymentDTo> getAllPayments() {
        List<Payment> list = paymentDAO.getAll();
        List<PaymentDTo> dtoList = new ArrayList<>();
        for (Payment payment : list) {
            dtoList.add(toDTO(payment));
        }
        return dtoList;
    }

    private PaymentDTo toDTO(Payment payment) {
        PaymentDTo dto = new PaymentDTo();
        dto.setId(payment.getPatient() != null ? payment.getPatient().getPatientId().toString() : null);
        dto.setPaymentId(payment.getPaymentId().toString());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getMethod() != null ? PaymentMethod.valueOf(payment.getMethod().name()) : null);
        dto.setPaymentStatus(payment.getStatus() != null ? PaymentStatus.valueOf(payment.getStatus().name()) : null);
        dto.setInvoiceNumber(payment.getInvoiceNumber());
        dto.setRemark(payment.getRemarks());
        dto.setCoveredSessions(null);
        return dto;
    }
}
