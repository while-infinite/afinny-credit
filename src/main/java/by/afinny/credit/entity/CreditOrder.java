package by.afinny.credit.entity;

import by.afinny.credit.entity.constant.CreditOrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = CreditOrder.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditOrder {

    public static final String TABLE_NAME = "credit_order";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "number", length = 20)
    private String number;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CreditOrderStatus status;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "period_months", nullable = false)
    private Integer periodMonths;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "monthly_income", precision = 19, scale = 4)
    private BigDecimal monthlyIncome;

    @Column(name = "monthly_expenditure", precision = 19, scale = 4)
    private BigDecimal monthlyExpenditure;

    @Column(name = "employer_identification_number", length = 10)
    private String employerIdentificationNumber;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "product_id")
    private Product product;
}
