package by.afinny.credit.dto;

import by.afinny.credit.entity.constant.CardStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PUBLIC)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RequestCardStatusDto {

    private String cardNumber;
    private CardStatus cardStatus;
}
