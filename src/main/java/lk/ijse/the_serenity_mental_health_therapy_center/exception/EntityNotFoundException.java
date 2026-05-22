package lk.ijse.the_serenity_mental_health_therapy_center.exception;

public class EntityNotFoundException extends SerenityException{
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " with ID [" + id + "] not found.");
    }
    public EntityNotFoundException(String message) { super(message); }
}
