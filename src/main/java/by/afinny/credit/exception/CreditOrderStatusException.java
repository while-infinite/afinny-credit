package by.afinny.credit.exception;

public class CreditOrderStatusException extends RuntimeException {

    public CreditOrderStatusException(String message) {
        super(message, null, false, false);
    }
}