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
@Setter
@ToString
public class LoanCalculationDto {

    BigDecimal principal;
    BigDecimal interest;
    BigDecimal principalDebt;
    BigDecimal interestDebt;
    LocalDate paymentDate;
}
