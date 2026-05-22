package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Patient;

import java.util.List;

public interface PatientDAO extends CrudDAO<Patient> {
    List<Patient> getPatientsEnrolledInAllPrograms();
    List<Patient> getPatientsWithPrograms();
}
