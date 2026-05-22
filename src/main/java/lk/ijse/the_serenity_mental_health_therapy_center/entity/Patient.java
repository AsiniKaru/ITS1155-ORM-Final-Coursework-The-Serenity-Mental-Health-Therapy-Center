package lk.ijse.the_serenity_mental_health_therapy_center.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "medical_history", length = 2000)
    private String medicalHistory;

    @Column(name = "registered_date" , updatable = false)
    private LocalDate registeredDate = LocalDate.now();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PatientTherapyProgram> PatientTherapyPrograms = new ArrayList<>();


    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TherapySession> sessions = new ArrayList<>();

//    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
//    @ToString.Exclude
//    private List<Payment> payments = new ArrayList<>();

    public String getFullPatientName() { return firstName + " " + lastName; }
}
