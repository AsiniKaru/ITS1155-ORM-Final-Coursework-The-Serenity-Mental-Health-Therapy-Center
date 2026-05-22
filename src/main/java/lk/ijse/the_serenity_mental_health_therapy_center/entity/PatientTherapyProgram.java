package lk.ijse.the_serenity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Data
@Table(name="patient_therapy_programs")
public class PatientTherapyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private TherapyProgram program;

    @Column(name = "upfront_sessions_paid")
    private int upfrontSessionsPaid = 0;

    @Column(name = "sessions_used")
    private int sessionsUsed = 0;

    public int getRemainingCredit() {
        return upfrontSessionsPaid - sessionsUsed;
    }

    public PatientTherapyProgram(Patient patient, TherapyProgram program, int upfrontSessionsPaid) {
        this.patient = patient;
        this.program = program;
        this.upfrontSessionsPaid = upfrontSessionsPaid;
        this.sessionsUsed = 0;
    }
}
