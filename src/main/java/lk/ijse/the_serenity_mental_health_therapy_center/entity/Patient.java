package lk.ijse.the_serenity_mental_health_therapy_center.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private int id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private int age;

    @Column(length = 1000)
    private String medicalHistory;


//    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Registration> registrations = new ArrayList<>();


//    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Session> sessions = new ArrayList<>();
}
