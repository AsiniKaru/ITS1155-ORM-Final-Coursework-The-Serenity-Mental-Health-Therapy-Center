package lk.ijse.the_serenity_mental_health_therapy_center.util;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
    }


    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
