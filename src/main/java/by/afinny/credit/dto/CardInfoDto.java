package by.afinny.credit.dto;

import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.entity.constant.PaymentSystem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PUBLIC)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CardInfoDto {

    private UUID creditId;
    private String accountNumber;
    private String cardNumber;
    private BigDecimal balance;
    private String holderName;
    private String expirationDate;
    private PaymentSystem paymentSystem;
    private CardStatus status;
    private BigDecimal transactionLimit;
    private String name;
    private BigDecimal principalDebt;
    private BigDecimal creditLimit;
    private String creditCurrencyCode;
    private String terminationDate;
}
