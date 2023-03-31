package by.afinny.credit.utils;


import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CalculationMode;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.entity.constant.CreditOrderStatus;
import by.afinny.credit.entity.constant.CreditType;
import by.afinny.credit.entity.constant.PaymentSystem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.afinny.credit.entity.constant.CreditStatus.ACTIVE;

@Component
public class InitClass {

    private final UUID CLIENT_ID = UUID.fromString("9af9fe9a-23b7-4d55-b3dc-a929b86c87b4");

    public Product setUpProduct() {


        return Product.builder()
                .name("productName")
                .minSum(BigDecimal.ONE)
                .maxSum(BigDecimal.TEN)
                .currencyCode("RUB")
                .minInterestRate(BigDecimal.valueOf(21))
                .maxInterestRate(BigDecimal.valueOf(33))
                .needGuarantees(true)
                .deliveryInCash(false)
                .earlyRepayment(false)
                .needIncomeDetails(false)
                .minPeriodMonths(2)
                .maxPeriodMonths(5)
                .isActive(true)
                .details("details")
                .calculationMode(CalculationMode.ANNUITY)
                .gracePeriodMonths(3)
                .rateIsAdjustable(false)
                .rateBase("0123")
                .rateFixPart(BigDecimal.valueOf(4))
                .increasedRate(BigDecimal.valueOf(66)).build();
    }

    public CreditOrder setUpCreditOrder() {
        return CreditOrder.builder()
                .number("1234567")
                .clientId(CLIENT_ID)
                .status(CreditOrderStatus.PENDING)
                .amount(BigDecimal.valueOf(33))
                .periodMonths(6)
                .creationDate(LocalDate.of(2021, 10, 30))
                .monthlyIncome(BigDecimal.valueOf(11))
                .monthlyExpenditure(BigDecimal.valueOf(22))
                .employerIdentificationNumber("12345")
                .build();
    }

    public Credit setUpCredit() {
        return Credit.builder()
                .type(CreditType.CONSUMER)
                .creditLimit(new BigDecimal("5000.00"))
                .currencyCode("RUB")
                .interestRate(new BigDecimal("5.00"))
                .personalGuarantees(true)
                .gracePeriodMonths(10)
                .status(ACTIVE)
                .latePaymentRate(new BigDecimal("5.00"))
                .build();
    }

    public Agreement setUpAgreement() {
        return Agreement.builder()
                .number("25")
                .agreementDate(LocalDate.now())
                .terminationDate(LocalDate.now().plusYears(1))
                .responsibleSpecialistId("554654")
                .isActive(true)
                .build();
    }

    public Account setUpAccount() {
        Card card = new Card();
        List<PaymentSchedule> paymentScheduleList = new ArrayList<>();
        return (Account.builder()
                .accountNumber("8778984")
                .principalDebt(new BigDecimal("10.00"))
                .interestDebt(new BigDecimal("15.00"))
                .isActive(true)
                .openingDate(LocalDate.now())
                .closingDate(LocalDate.now().plusYears(1))
                .currencyCode("RUB")
                .card(card)
                .outstandingPrincipal(new BigDecimal("4.00"))
                .outstandingInterestDebt(new BigDecimal("5.00"))
                .paymentSchedules(paymentScheduleList)
                .build());
    }

    public PaymentSchedule setUpPaymentSchedule() {
        return PaymentSchedule.builder()
                .paymentDate(LocalDate.of(2023, 4, 20))
                .principal(new BigDecimal("500.00"))
                .interest((new BigDecimal("10.00")))
                .build();
    }

    public Card setUpCard() {
        String CARD_NUMBER = "5855155555555555";
        return (Card.builder()
                .cardNumber(CARD_NUMBER)
                .holderName("Tolik")
                .expirationDate(LocalDate.now())
                .paymentSystem(PaymentSystem.VISA)
                .balance(new BigDecimal("5000.00"))
                .status(CardStatus.ACTIVE)
                .transactionLimit(new BigDecimal("50.00"))
                .deliveryPoint("sdsf")
                .isDigitalWallet(true)
                .isVirtual(true)
                .coBrand("co_brand")
                .build());
    }
}