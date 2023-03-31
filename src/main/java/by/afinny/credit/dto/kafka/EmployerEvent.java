package by.afinny.credit.dto.kafka;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PUBLIC)
public class EmployerEvent {

    private UUID clientId;
    private String employerIdentificationNumber;
}