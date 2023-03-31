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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class RequestCreditOrderDto {

    private Integer productId;
    private BigDecimal amount;
    private Integer periodMonths;
    private LocalDate creationDate;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenditure;
    private String employerIdentificationNumber;
}
