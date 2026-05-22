package lk.ijse.the_serenity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="therapy_session")
public class TherapySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private TherapyProgram therapyProgram;

    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Column(name = "session_time")
    private LocalTime sessionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "upfront_amount", precision = 12, scale = 2)
    private BigDecimal upfrontAmount;

    @Column(name = "notes", length = 1000)
    private String notes;

    public enum SessionStatus {
        SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED
    }


}
