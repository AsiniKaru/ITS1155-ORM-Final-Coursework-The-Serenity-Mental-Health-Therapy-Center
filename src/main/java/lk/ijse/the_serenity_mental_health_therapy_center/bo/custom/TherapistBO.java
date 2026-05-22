package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;

import java.util.List;

public interface TherapistBO extends SuperBO {
    boolean saveTherapist(TherapistDTO dto);
    boolean updateTherapist(TherapistDTO dto);
    boolean deleteTherapist(String id);
    TherapistDTO searchTherapist(String id);
    List<TherapistDTO> getAllTherapists();
    boolean assignProgramToTherapist(String therapistId, String programId);
}
