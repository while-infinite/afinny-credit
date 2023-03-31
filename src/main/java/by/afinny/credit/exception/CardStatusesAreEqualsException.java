package by.afinny.credit.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PUBLIC)
public class CardStatusesAreEqualsException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

    public CardStatusesAreEqualsException(String errorCode, String errorMessage) {
        super(errorCode + ": " + errorMessage, null, false, false);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
