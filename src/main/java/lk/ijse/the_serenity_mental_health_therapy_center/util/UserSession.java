package lk.ijse.the_serenity_mental_health_therapy_center.util;

import lk.ijse.the_serenity_mental_health_therapy_center.dto.UserDTO;

public class UserSession {
    private static UserSession instance;
    private UserDTO loggedInUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public UserDTO getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserDTO loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void cleanUserSession() {
        loggedInUser = null;
    }
}
