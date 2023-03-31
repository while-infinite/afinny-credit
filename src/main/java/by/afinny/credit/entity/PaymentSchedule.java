package by.afinny.credit.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = PaymentSchedule.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSchedule {

    public static final String TABLE_NAME = "payment_schedule";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "principal", nullable = false, precision = 19, scale = 4)
    private BigDecimal principal;

    @Column(name = "interest", nullable = false, precision = 19, scale = 4)
    private BigDecimal interest;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id")
    private Account account;
}
