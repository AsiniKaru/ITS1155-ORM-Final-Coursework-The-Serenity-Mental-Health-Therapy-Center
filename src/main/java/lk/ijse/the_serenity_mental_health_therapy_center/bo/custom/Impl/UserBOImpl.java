package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.UserDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.UserRole;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.User;
import lk.ijse.the_serenity_mental_health_therapy_center.exception.InvalidCredentialsException;
import lk.ijse.the_serenity_mental_health_therapy_center.util.PasswordUtil;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class UserBOImpl implements UserBO {

    private final UserDAO userDAO = (UserDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.USER);

    @Override
    public UserDTO login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null || !user.isActive()) {
            throw new InvalidCredentialsException("User not found or inactive.");
        }
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        return new UserDTO(
                user.getId().intValue(), user.getUsername(), null,
                user.getFName(), user.getLName(), user.getEmail(),
                UserRole.valueOf(user.getRole().name()), user.isActive()
        );
    }

    @Override
    public boolean registerUser(UserDTO dto) {
        ValidationUtil.checkRequiredField(dto.getUsername(), "Username");
        ValidationUtil.checkRequiredField(dto.getPassword(), "Password");
        ValidationUtil.checkRequiredField(dto.getFirstName(), "First Name");
        ValidationUtil.checkRequiredField(dto.getLastName(), "Last Name");
        ValidationUtil.validateEmail(dto.getEmail());

        if (userDAO.findByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(PasswordUtil.hashPassword(dto.getPassword()));
        user.setFName(dto.getFirstName());
        user.setLName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setRole(User.UserRole.valueOf(dto.getRole().name()));
        user.setActive(dto.isActive());

        return userDAO.save(user);
    }

    @Override
    public boolean updateUser(UserDTO dto) {
        ValidationUtil.checkRequiredField(dto.getUsername(), "Username");
        ValidationUtil.checkRequiredField(dto.getFirstName(), "First Name");
        ValidationUtil.checkRequiredField(dto.getLastName(), "Last Name");
        ValidationUtil.validateEmail(dto.getEmail());

        User existingUser = userDAO.search(String.valueOf(dto.getId()));
        if (existingUser == null) return false;

        // Check if the username is being changed and if the new username is already in use
        if (!existingUser.getUsername().equals(dto.getUsername())) {
            User conflictUser = userDAO.findByUsername(dto.getUsername());
            if (conflictUser != null) {
                throw new RuntimeException("New username is already taken!");
            }
            existingUser.setUsername(dto.getUsername());
        }

        existingUser.setFName(dto.getFirstName());
        existingUser.setLName(dto.getLastName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setRole(User.UserRole.valueOf(dto.getRole().name()));
        existingUser.setActive(dto.isActive());

        // If a new password was provided, hash and update it
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingUser.setPasswordHash(PasswordUtil.hashPassword(dto.getPassword()));
        }

        return userDAO.update(existingUser);
    }

    @Override
    public boolean deleteUser(String userId) {
        return userDAO.delete(userId);
    }

    @Override
    public ArrayList<UserDTO> searchUser(String userId) {
        User user = userDAO.search(userId);
        ArrayList<UserDTO> list = new ArrayList<>();
        if (user != null) {
            list.add(new UserDTO(
                    user.getId().intValue(), user.getUsername(), null,
                    user.getFName(), user.getLName(), user.getEmail(),
                    UserRole.valueOf(user.getRole().name()), user.isActive()
            ));
        }
        return list;
    }

    @Override
    public ArrayList<UserDTO> getAllUsers() {
        List<User> entityList = userDAO.getAll();
        ArrayList<UserDTO> dtoList = new ArrayList<>();
        for (User user : entityList) {
            dtoList.add(new UserDTO(
                    user.getId().intValue(), user.getUsername(), null,
                    user.getFName(), user.getLName(), user.getEmail(),
                    UserRole.valueOf(user.getRole().name()), user.isActive()
            ));
        }
        return dtoList;
    }

    @Override
    public UserDTO verifyIdentity(String username, String email) {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getEmail().equalsIgnoreCase(email)) {
            return new UserDTO(
                    user.getId().intValue(), user.getUsername(), null,
                    user.getFName(), user.getLName(), user.getEmail(),
                    UserRole.valueOf(user.getRole().name()), user.isActive()
            );
        }
        throw new InvalidCredentialsException("Username and Email combination do not match.");
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found for password reset.");
        }
        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        userDAO.update(user);
    }

    @Override
    public boolean emailExists(String email) {
        List<User> all = userDAO.getAll();
        for (User u : all) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}
