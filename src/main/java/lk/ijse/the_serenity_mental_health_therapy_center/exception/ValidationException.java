package lk.ijse.the_serenity_mental_health_therapy_center.exception;

public class ValidationException extends SerenityException {
    private final String field;

    public ValidationException(String field, String message) {
        super("Validation failed for [" + field + "]: " + message);
        this.field = field;
    }

    public String getField() { return field; }
}
