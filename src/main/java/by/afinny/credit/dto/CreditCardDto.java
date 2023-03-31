package by.afinny.credit.dto;

import by.afinny.credit.entity.constant.PaymentSystem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class CreditCardDto {

    private UUID id;
    private String accountNumber;
    private String cardNumber;
    private BigDecimal balance;
    private String currencyCode;
    private PaymentSystem paymentSystem;
    private LocalDate expirationDate;
    private String name;
}