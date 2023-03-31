package by.afinny.credit.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = Account.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    public static final String TABLE_NAME = "account";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "principal_debt", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDebt;

    @Column(name = "interest_debt", nullable = false, precision = 19, scale = 4)
    private BigDecimal interestDebt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;

    @OneToOne(mappedBy = "account")
    private Card card;

    @OneToMany(mappedBy = "account")
    private List<PaymentSchedule> paymentSchedules;

    @Column(name = "outstanding_principal", precision = 19, scale = 4)
    private BigDecimal outstandingPrincipal;

    @Column(name = "outstanding_interest_debt", precision = 19, scale = 4)
    private BigDecimal outstandingInterestDebt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
