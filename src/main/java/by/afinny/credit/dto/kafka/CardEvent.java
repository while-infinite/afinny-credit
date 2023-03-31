package by.afinny.credit.dto.kafka;

import by.afinny.credit.entity.constant.CardStatus;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
public class CardEvent {

    private UUID clientId;
    private String cardNumber;
    private CardStatus cardStatus;
}
