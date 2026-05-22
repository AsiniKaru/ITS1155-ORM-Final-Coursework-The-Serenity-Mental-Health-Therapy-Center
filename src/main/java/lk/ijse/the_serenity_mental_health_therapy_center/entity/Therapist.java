package lk.ijse.the_serenity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="therapist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Therapist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "therapist_id")
    private Integer therapistId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Availability status = Availability.ACTIVE;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "therapist_program",
            joinColumns = @JoinColumn(name = "therapist_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private List<TherapyProgram> programs = new ArrayList<>();

    @OneToMany(mappedBy = "therapist", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<TherapySession> sessions = new ArrayList<>();

    public enum Availability {
        ACTIVE, INACTIVE
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
