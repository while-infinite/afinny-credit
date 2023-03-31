package by.afinny.credit.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class DetailsDto {

    private UUID agreementId;
    private String creditCurrencyCode;
    private BigDecimal principalDebt;
    private BigDecimal interestDebt;
    private BigDecimal principal;
    private BigDecimal interest;
}
