package lk.ijse.the_serenity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<TherapySession> coveredSessions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate =  LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 20)
    private Method method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "invoice_number", unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "remarks", length = 300)
    private String remarks;

    public enum Method {
        CASH, CARD, BANK_TRANSFER
    }

    public enum PaymentStatus {
        PENDING,UPFRONT, COMPLETED
    }


}
