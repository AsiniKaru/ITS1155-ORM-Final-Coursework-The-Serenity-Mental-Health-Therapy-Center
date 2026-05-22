package lk.ijse.the_serenity_mental_health_therapy_center.dto;


import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.SessionStatus;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapySession;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapySessionDTO {
    private String sessionId;
    private String patientId;
    private String therapistId;
    private String programId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private SessionStatus sessionStatus;
    private BigDecimal upfrontPayment;
    private String note;
    private String paymentId;
    private String paymentStatus;





}
