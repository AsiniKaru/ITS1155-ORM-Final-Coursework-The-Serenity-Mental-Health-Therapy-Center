package lk.ijse.the_serenity_mental_health_therapy_center.dto;

import lk.ijse.the_serenity_mental_health_therapy_center.entity.PatientTherapyProgram;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapySession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private String patientId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String medicalHistory;
    private LocalDate registeredDate;
    private List<PatientTherapyProgram> therapyPrograms;
    private List<TherapySession> therapySessions;
}
