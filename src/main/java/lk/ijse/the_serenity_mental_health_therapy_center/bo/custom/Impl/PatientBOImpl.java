package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.PatientBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Patient;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientBOImpl implements PatientBO {

    private final PatientDAO patientDAO = (PatientDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);

    @Override
    public boolean savePatient(PatientDTO dto) {
        // Validate Inputs
        ValidationUtil.checkRequiredField(dto.getFirstName(), "First Name");
        ValidationUtil.checkRequiredField(dto.getLastName(), "Last Name");
        ValidationUtil.validateEmail(dto.getEmail());
        ValidationUtil.validatePhone(dto.getPhoneNumber());

        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhoneNumber());
        patient.setAddress(dto.getAddress());
        patient.setMedicalHistory(dto.getMedicalHistory());
        if (dto.getRegisteredDate() != null) {
            patient.setRegisteredDate(dto.getRegisteredDate());
        }

        return patientDAO.save(patient);
    }

    @Override
    public boolean updatePatient(PatientDTO dto) {
        ValidationUtil.checkRequiredField(dto.getFirstName(), "First Name");
        ValidationUtil.checkRequiredField(dto.getLastName(), "Last Name");
        ValidationUtil.validateEmail(dto.getEmail());
        ValidationUtil.validatePhone(dto.getPhoneNumber());

        Patient patient = patientDAO.search(dto.getPatientId());
        if (patient == null) return false;

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhoneNumber());
        patient.setAddress(dto.getAddress());
        patient.setMedicalHistory(dto.getMedicalHistory());

        return patientDAO.update(patient);
    }

    @Override
    public boolean deletePatient(String id) {
        return patientDAO.delete(id);
    }

    @Override
    public PatientDTO searchPatient(String id) {
        Patient patient = patientDAO.search(id);
        if (patient == null) return null;
        return toDTO(patient);
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientDAO.getAll();
        List<PatientDTO> dtoList = new ArrayList<>();
        for (Patient patient : patients) {
            dtoList.add(toDTO(patient));
        }
        return dtoList;
    }

    @Override
    public List<PatientDTO> getPatientsEnrolledInAllPrograms() {
        List<Patient> patients = patientDAO.getPatientsEnrolledInAllPrograms();
        List<PatientDTO> dtoList = new ArrayList<>();
        for (Patient patient : patients) {
            dtoList.add(toDTO(patient));
        }
        return dtoList;
    }

    @Override
    public List<PatientDTO> getPatientsWithPrograms() {
        List<Patient> patients = patientDAO.getPatientsWithPrograms();
        List<PatientDTO> dtoList = new ArrayList<>();
        for (Patient patient : patients) {
            dtoList.add(toDTO(patient));
        }
        return dtoList;
    }

    private PatientDTO toDTO(Patient patient) {
        return new PatientDTO(
                patient.getPatientId().toString(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getMedicalHistory(),
                patient.getRegisteredDate(),
                patient.getPatientTherapyPrograms(),
                patient.getSessions()
        );
    }
}
