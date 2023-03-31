package by.afinny.credit.dto;

import by.afinny.credit.entity.constant.CalculationMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class ProductDto {

    private Integer id;
    private String name;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private String currencyCode;
    private BigDecimal minInterestRate;
    private BigDecimal maxInterestRate;
    private Boolean needGuarantees;
    private Boolean deliveryInCash;
    private Boolean earlyRepayment;
    private Boolean needIncomeDetails;
    private Integer minPeriodMonths;
    private Integer maxPeriodMonths;
    private Boolean isActive;
    private String details;
    private CalculationMode calculationMode;
    private Integer gracePeriodMonths;
    private Boolean rateIsAdjustable;
    private String rateBase;
    private BigDecimal rateFixPart;
    private BigDecimal increasedRate;
}

