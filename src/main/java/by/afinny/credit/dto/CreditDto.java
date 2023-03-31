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
public class CreditDto {

    private UUID creditId;
    private String name;
    private BigDecimal principalDebt;
    private BigDecimal creditLimit;
    private String creditCurrencyCode;
    private LocalDate terminationDate;
}
