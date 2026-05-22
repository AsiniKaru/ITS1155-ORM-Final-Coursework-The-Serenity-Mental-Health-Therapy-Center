package lk.ijse.the_serenity_mental_health_therapy_center.dto;

import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.PaymentMethod;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.PaymentStatus;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTo {
    private String id;
    private String paymentId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String invoiceNumber;
    private String remark;
    private List<TherapySessionDTO> coveredSessions;
    private String programId;
    private Integer sessionsToBuy;
}
