package by.afinny.credit.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message, null, false, false);
    }
}
