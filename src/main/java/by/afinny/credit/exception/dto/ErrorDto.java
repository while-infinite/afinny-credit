package by.afinny.credit.exception.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
public class ErrorDto {

    private final String errorCode;
    private final String errorMessage;
    private final String errorClass;
    private final String errorMethod;
}
