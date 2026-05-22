package lk.ijse.the_serenity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "therapy_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Integer programId;

    @Column(name = "program_code", unique = true, nullable = false, length = 20)
    private String programCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "duration_weeks", nullable = false)
    private String durationWeeks;

    @Column(name = "fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal fee;

    @OneToMany(mappedBy = "program", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<PatientTherapyProgram> patientTherapyPrograms  = new ArrayList<>();

//    @OneToMany(mappedBy = "therapyProgram", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
//    private List<TherapySession> sessions = new ArrayList<>();

    @ManyToMany(mappedBy = "programs", fetch = FetchType.LAZY)
    private List<Therapist> therapists = new ArrayList<>();

}
