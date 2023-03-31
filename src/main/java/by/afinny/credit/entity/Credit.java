package by.afinny.credit.entity;

import by.afinny.credit.entity.constant.CreditStatus;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = Credit.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit {

    public static final String TABLE_NAME = "credit";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private CreditType type;

    @Column(name = "credit_limit", precision = 19, scale = 4)
    private BigDecimal creditLimit;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "interest_rate", nullable = false, precision = 19, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "personal_guarantees")
    private Boolean personalGuarantees;

    @Column(name = "grace_period_months")
    private Integer gracePeriodMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CreditStatus status;

    @OneToOne(mappedBy = "credit")
    private Account account;

    @OneToOne(mappedBy = "credit")
    private Agreement agreement;

    @OneToOne
    @JoinColumn(name = "order_id")
    private CreditOrder creditOrder;

    @Column(name = "late_payment_rate", precision = 6, scale = 4)
    private BigDecimal latePaymentRate;
}
