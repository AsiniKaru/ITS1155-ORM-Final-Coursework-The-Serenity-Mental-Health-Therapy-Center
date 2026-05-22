package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;

import java.util.ArrayList;

public interface UserBO extends SuperBO {

    UserDTO login(String username , String password);
    boolean registerUser(UserDTO dto);
    boolean updateUser(UserDTO dto);
    boolean deleteUser(String userId);
    ArrayList<UserDTO> searchUser(String userId);
    ArrayList<UserDTO> getAllUsers();
    UserDTO verifyIdentity(String username, String email);
    void resetPassword(String username, String newPassword);
    boolean emailExists(String email);

}
