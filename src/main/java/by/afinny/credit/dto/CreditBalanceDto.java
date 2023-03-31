package by.afinny.credit.dto;

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
public class CreditBalanceDto {

    private String name;
    private String creditCurrencyCode;
    private LocalDate agreementDate;
    private String agreementNumber;
    private UUID agreementId;
    private String accountNumber;
    private CreditCardBalanceDto card;
    private String accountCurrencyCode;
    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private BigDecimal principalDebt;
    private BigDecimal interestDebt;
    private LocalDate paymentDate;
    private BigDecimal paymentPrincipal;
    private BigDecimal paymentInterest;
}