package by.afinny.credit.exception.handler;

import by.afinny.credit.exception.CardBalanceIsNotEqualsCreditLimitException;
import by.afinny.credit.exception.CardStatusesAreEqualsException;
import by.afinny.credit.exception.CreditOrderStatusException;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.exception.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    private final String INTERNAL_SERVER_ERROR = "Internal server error";
    private final String BAD_REQUEST = "Bad request";

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> serverExceptionHandler(Exception e) {
        log.error(INTERNAL_SERVER_ERROR + e.getMessage());
        return createResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                createErrorDto(e, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR));
    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CardStatusesAreEqualsException.class)
    public ResponseEntity<ErrorDto> cardStatusesAreEqualsExceptionHandler(CardStatusesAreEqualsException e) {
        log.error(BAD_REQUEST, e);
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                createErrorDto(e, HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        log.error(BAD_REQUEST, e);
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                createErrorDto(e, HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CreditOrderStatusException.class)
    public ResponseEntity<ErrorDto> creditOrderStatusExceptionHandler(CreditOrderStatusException e) {
        log.error(BAD_REQUEST, e);
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                createErrorDto(e, HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CardBalanceIsNotEqualsCreditLimitException.class)
    public ResponseEntity<ErrorDto> cardBalanceIsNotEqualsCreditLimitExceptionHandler(CardBalanceIsNotEqualsCreditLimitException e) {
        log.error(BAD_REQUEST, e);
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                createErrorDto(e, HttpStatus.BAD_REQUEST, e.getErrorMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorDto> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        log.error("Invalid parameter. " + ex.getMessage());
        if (Objects.requireNonNull(ex.getMessage()).contains("The given id must not be null!")) {
            ErrorDto errorDto = createErrorDto(ex, HttpStatus.BAD_REQUEST, "The ID parameter is required");
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        } else {
            ErrorDto errorDto = createErrorDto(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Throwable cause = e.getCause();
        if (cause instanceof PropertyValueException) {
            String property = ((PropertyValueException) cause).getPropertyName();
            ErrorDto errorDto = createErrorDto(e, HttpStatus.BAD_REQUEST, String.format("The property '%s' is required", property));
            log.error(String.format("The property '%s' is required", property));
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        } else {
            log.error("The property is required. " + e.getMessage());
            ErrorDto errorDto = createErrorDto(e, HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }
    }


    private ResponseEntity<ErrorDto> createResponseEntity(HttpStatus status, ErrorDto errorDto) {
        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(errorDto);
    }

    private ErrorDto createErrorDto(Exception e, HttpStatus httpStatus, String errorMessage) {
        if(e.getStackTrace().length == 0){
            return ErrorDto.builder()
                    .errorCode(String.valueOf(httpStatus))
                    .errorMessage(errorMessage)
                    .build();
        }
        StackTraceElement element = e.getStackTrace()[2];
        String[] splitArray = element.getClassName().split("\\.");
        int arrayLength = splitArray.length;

        return ErrorDto.builder()
                .errorCode(String.valueOf(httpStatus))
                .errorMessage(errorMessage)
                .errorClass(splitArray[arrayLength - 1])
                .errorMethod(element.getMethodName())
                .build();
    }
}
