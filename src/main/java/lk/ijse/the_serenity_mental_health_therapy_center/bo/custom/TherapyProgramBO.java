package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;

import java.util.List;

public interface TherapyProgramBO extends SuperBO {
    boolean saveTherapyProgram(TherapyProgramDTO dto);
    boolean updateTherapyProgram(TherapyProgramDTO dto);
    boolean deleteTherapyProgram(String id);
    TherapyProgramDTO searchTherapyProgram(String id);
    List<TherapyProgramDTO> getAllTherapyPrograms();
}
