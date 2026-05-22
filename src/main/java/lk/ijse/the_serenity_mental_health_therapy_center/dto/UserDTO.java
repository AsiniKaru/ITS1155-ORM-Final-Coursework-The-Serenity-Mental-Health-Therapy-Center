package lk.ijse.the_serenity_mental_health_therapy_center.dto;


import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private boolean active;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}