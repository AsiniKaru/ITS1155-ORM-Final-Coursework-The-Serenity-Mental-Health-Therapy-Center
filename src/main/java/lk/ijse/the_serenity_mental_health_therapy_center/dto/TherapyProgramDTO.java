package lk.ijse.the_serenity_mental_health_therapy_center.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapyProgramDTO {
    private String programId;
    private String programCode;
    private String programName;
    private String description;
    private String duration;
    private BigDecimal programFee;
//    private List<PatientTherapyProgramDTO> patientTherapyPrograms;
    private List<TherapistDTO> therapists;


}
