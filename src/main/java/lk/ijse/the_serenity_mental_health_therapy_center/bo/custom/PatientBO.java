package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PatientDTO;

import java.util.List;

public interface PatientBO extends SuperBO {
    boolean savePatient(PatientDTO dto);
    boolean updatePatient(PatientDTO dto);
    boolean deletePatient(String id);
    PatientDTO searchPatient(String id);
    List<PatientDTO> getAllPatients();
    List<PatientDTO> getPatientsEnrolledInAllPrograms();
    List<PatientDTO> getPatientsWithPrograms();
}
