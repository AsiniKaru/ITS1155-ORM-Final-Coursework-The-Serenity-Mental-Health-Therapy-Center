package lk.ijse.the_serenity_mental_health_therapy_center.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String fName;

    @Column(nullable = false, length = 100)
    private String lName;

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public enum UserRole {
        ADMIN, RECEPTIONIST
    }
}
