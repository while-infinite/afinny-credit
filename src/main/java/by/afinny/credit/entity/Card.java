package by.afinny.credit.entity;

import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.entity.constant.PaymentSystem;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = Card.TABLE_NAME)
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    public static final String TABLE_NAME = "card";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "holder_name", nullable = false, length = 50)
    private String holderName;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_system", nullable = false, length = 30)
    private PaymentSystem paymentSystem;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CardStatus status;

    @Column(name = "transaction_limit", precision = 19, scale = 4)
    private BigDecimal transactionLimit;

    @Column(name = "delivery_point", nullable = false, length = 30)
    private String deliveryPoint;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "is_digital_wallet")
    private Boolean isDigitalWallet;

    @Column(name = "is_virtual")
    private Boolean isVirtual;

    @Column(name = "co_brand", length = 30)
    private String coBrand;
}
