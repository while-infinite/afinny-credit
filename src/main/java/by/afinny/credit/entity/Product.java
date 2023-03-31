package by.afinny.credit.entity;

import by.afinny.credit.entity.constant.CalculationMode;
import by.afinny.credit.entity.constant.CreditType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = Product.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    public static final String TABLE_NAME = "product";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "min_sum", nullable = false, precision = 19, scale = 4)
    private BigDecimal minSum;

    @Column(name = "max_sum", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxSum;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "min_interest_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal minInterestRate;

    @Column(name = "max_interest_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal maxInterestRate;

    @Column(name = "need_guarantees", nullable = false)
    private Boolean needGuarantees;

    @Column(name = "delivery_in_cash", nullable = false)
    private Boolean deliveryInCash;

    @Column(name = "early_repayment", nullable = false)
    private Boolean earlyRepayment;

    @Column(name = "need_income_details", nullable = false)
    private Boolean needIncomeDetails;

    @Column(name = "min_period_months", nullable = false)
    private Integer minPeriodMonths;

    @Column(name = "max_period_months", nullable = false)
    private Integer maxPeriodMonths;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "details", nullable = false)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_mode", nullable = false, length = 30)
    private CalculationMode calculationMode;

    @Column(name = "grace_period_months")
    private Integer gracePeriodMonths;

    @Column(name = "rate_is_adjustable")
    private Boolean rateIsAdjustable;

    @Column(name = "rate_base", length = 20)
    private String rateBase;

    @Column(name = "rate_fix_part", precision = 6, scale = 4)
    private BigDecimal rateFixPart;

    @Column(name = "increased_rate", precision = 6, scale = 4)
    private BigDecimal increasedRate;

    @Column(name = "auto_processing", nullable = false)
    private Boolean autoProcessing;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_credit", nullable = false, length = 30)
    private CreditType typeCredit;
}
