package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapySessionDTO;

import java.util.List;

public interface TherapySessionBO extends SuperBO {
    boolean saveSession(TherapySessionDTO dto);
    boolean updateSession(TherapySessionDTO dto);
    boolean deleteSession(String id);
    TherapySessionDTO searchSession(String id);
    List<TherapySessionDTO> getAllSessions();
}
