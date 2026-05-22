package lk.ijse.the_serenity_mental_health_therapy_center.dto;

import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.TherapistAvailability;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Therapist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapistDTO {
    private String therapistId;
    private String therapistFirstName;
    private String therapistLastName;
    private String email;
    private String phone;
    private String specialization;
    private TherapistAvailability availability;
    private List<TherapyProgramDTO> programIds;
    private List<TherapySessionDTO> sessionIds;
}
