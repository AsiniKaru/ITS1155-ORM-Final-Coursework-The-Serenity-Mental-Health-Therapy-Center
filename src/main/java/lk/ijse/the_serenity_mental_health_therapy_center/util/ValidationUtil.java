package lk.ijse.the_serenity_mental_health_therapy_center.util;

import lk.ijse.the_serenity_mental_health_therapy_center.exception.ValidationException;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Sri Lankan phone number validation pattern (supports 07xxxxxxxx, +947xxxxxxxx, 947xxxxxxxx)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(?:\\+94|0)?7[0-9]{8}$"
    );

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", "Email address is required.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("email", "Invalid email format (e.g. name@domain.com).");
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("phone", "Phone number is required.");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("phone", "Invalid phone number format. Please enter a valid mobile number.");
        }
    }

    public static void checkRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " is required.");
        }
    }

    public static void checkPositiveAmount(java.math.BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName, fieldName + " must be a positive number.");
        }
    }
}
