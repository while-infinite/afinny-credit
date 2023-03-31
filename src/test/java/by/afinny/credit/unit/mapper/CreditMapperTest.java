package by.afinny.credit.unit.mapper;

import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditCardBalanceDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.PaymentScheduleDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditType;
import by.afinny.credit.mapper.CardMapper;
import by.afinny.credit.mapper.CreditMapperImpl;
import by.afinny.credit.mapper.ScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Verification of correct data generation. It will pass if the fields of the entity and dto are equal")
class CreditMapperTest {

    @InjectMocks
    private CreditMapperImpl creditMapper;

    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private CardMapper cardMapper;
    private Product product;
    private Credit credit;
    private Account account;
    private Agreement agreement;
    private PaymentSchedule paymentSchedule;
    private PaymentScheduleDto paymentScheduleDto;
    private List<PaymentSchedule> paymentScheduleList;
    private Card card;
    private CreditCardBalanceDto creditCardBalanceDto;

    @BeforeEach
    void setUp() {
        credit = Credit.builder()
                .id(UUID.randomUUID())
                .creditOrder(CreditOrder.builder()
                        .product(Product.builder()
                                .name("name")
                                .build())
                        .build())
                .account(Account.builder()
                        .principalDebt(new BigDecimal("10.0000"))
                        .build())
                .creditLimit(new BigDecimal("200.0000"))
                .currencyCode("RUB")
                .creditLimit(new BigDecimal("5000.00"))
                .interestRate(new BigDecimal("5.00"))
                .gracePeriodMonths(5)
                .type(CreditType.CONSUMER)
                .build();

        product = Product.builder()
                .name("name")
                .build();

        card = Card.builder()
                .cardNumber("58551")
                .balance(BigDecimal.TEN)
                .id(UUID.randomUUID())
                .build();

        agreement = Agreement.builder()
                .agreementDate(LocalDate.now().plusYears(1))
                .id(UUID.randomUUID())
                .terminationDate(LocalDate.now().plusYears(1))
                .number("55588")
                .build();

        account = Account.builder()
                .principalDebt(new BigDecimal("10.00"))
                .interestDebt(new BigDecimal("15.00"))
                .accountNumber("8778984")
                .currencyCode("RUB")
                .card(card)
                .build();

        paymentSchedule = new PaymentSchedule();
        paymentSchedule.setPaymentDate(LocalDate.of(2022, 4, 20));
        paymentSchedule.setPrincipal(new BigDecimal("500.00"));
        paymentSchedule.setInterest(new BigDecimal("10.00"));

        paymentScheduleDto = PaymentScheduleDto.builder().build();

        paymentScheduleList = List.of(paymentSchedule, paymentSchedule);

        creditCardBalanceDto = CreditCardBalanceDto.builder()
                .balance(card.getBalance())
                .cardId(card.getId())
                .cardNumber(card.getCardNumber())
                .build();
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void toCreditBalanceDto_checkCorrectMappingData() {
        when(cardMapper.toCreditCardDto(card)).thenReturn(creditCardBalanceDto);
        CreditBalanceDto creditBalanceDto = creditMapper.toCreditBalanceDto(product, credit,agreement, account, card, paymentSchedule);
        verifyCreditBalance(creditBalanceDto,product, credit,agreement, account, paymentSchedule);
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void creditToCreditDto_checkCorrectMappingData() {
        credit.setAgreement(agreement);
        credit.setAccount(account);
        CreditDto creditDto = creditMapper.creditToCreditDto(credit);
        verifyCreditDto(creditDto, account, agreement, credit);
    }

    @Test
    @DisplayName("Verification of correct Credit schedule data generation")
    void toCreditScheduleDto_checkCorrectMappingData() {
        when(scheduleMapper.toPaymentScheduleDto(any(PaymentSchedule.class)))
                .thenReturn(paymentScheduleDto);

        CreditScheduleDto creditScheduleDto = creditMapper.toCreditScheduleDto(agreement, account, paymentScheduleList);

        verifyCreditSchedule(creditScheduleDto, account, agreement, paymentScheduleList, paymentScheduleDto);
    }

    private void verifyCreditBalance(CreditBalanceDto creditBalanceDto, Product product, Credit credit, Agreement agreement,
                                     Account account, PaymentSchedule paymentSchedule) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(creditBalanceDto.getName())
                    .withFailMessage("Product name should be equals")
                    .isEqualTo(product.getName());
            softAssertions.assertThat(creditBalanceDto.getCreditLimit())
                    .withFailMessage("Credit limits should be equals")
                    .isEqualTo(credit.getCreditLimit());
            softAssertions.assertThat(creditBalanceDto.getCreditCurrencyCode())
                    .withFailMessage("Credit currency code should be equals")
                    .isEqualTo(credit.getCurrencyCode());
            softAssertions.assertThat(creditBalanceDto.getInterestRate())
                    .withFailMessage("Interest rates should be equals")
                    .isEqualTo(credit.getInterestRate());
            softAssertions.assertThat(creditBalanceDto.getAccountNumber())
                    .withFailMessage("Account number should be equals")
                    .isEqualTo(account.getAccountNumber());
            softAssertions.assertThat(creditBalanceDto.getAccountCurrencyCode())
                    .withFailMessage("Account currency code should be equals")
                    .isEqualTo(account.getCurrencyCode());
            softAssertions.assertThat(creditBalanceDto.getPrincipalDebt())
                    .withFailMessage("Principal debts should be equals")
                    .isEqualTo(account.getPrincipalDebt());
            softAssertions.assertThat(creditBalanceDto.getInterestDebt())
                    .withFailMessage("Interest debts should be equals")
                    .isEqualTo(account.getInterestDebt());
            softAssertions.assertThat(creditBalanceDto.getPaymentDate())
                    .withFailMessage("Payment dates should be equals")
                    .isEqualTo(paymentSchedule.getPaymentDate());
            softAssertions.assertThat(creditBalanceDto.getPaymentPrincipal())
                    .withFailMessage("Principals should be equals")
                    .isEqualTo(paymentSchedule.getPrincipal());
            softAssertions.assertThat(creditBalanceDto.getPaymentInterest())
                    .withFailMessage("Interests should be equals")
                    .isEqualTo(paymentSchedule.getInterest());
            softAssertions.assertThat(creditBalanceDto.getAgreementDate())
                    .withFailMessage("Agreement Date should be equals")
                    .isEqualTo(agreement.getAgreementDate());
            softAssertions.assertThat(creditBalanceDto.getAgreementNumber())
                    .withFailMessage("Agreement number should be equals")
                    .isEqualTo(agreement.getNumber());
            softAssertions.assertThat(creditBalanceDto.getAgreementId())
                    .withFailMessage("Agreement id should be equals")
                    .isEqualTo(agreement.getId());
            softAssertions.assertThat(creditBalanceDto.getCard())
                    .withFailMessage("Card should be equals")
                    .isEqualTo(creditCardBalanceDto);
        });
    }

    private void verifyCreditDto(CreditDto creditDto, Account account, Agreement agreement, Credit credit) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(creditDto.getCreditId())
                    .withFailMessage("Id should be equals")
                    .isEqualTo(credit.getId());
            softAssertions.assertThat(creditDto.getName())
                    .withFailMessage("Name should be equals")
                    .isEqualTo(credit.getCreditOrder().getProduct().getName());
            softAssertions.assertThat(creditDto.getPrincipalDebt())
                    .withFailMessage("Principal debts should be equals")
                    .isEqualTo(account.getPrincipalDebt());
            softAssertions.assertThat(creditDto.getCreditLimit())
                    .withFailMessage("Credit limits should be equals")
                    .isEqualTo(credit.getCreditLimit());
            softAssertions.assertThat(creditDto.getCreditCurrencyCode())
                    .withFailMessage("Currency codes should be equals")
                    .isEqualTo(credit.getCurrencyCode());
            softAssertions.assertThat(creditDto.getTerminationDate())
                    .withFailMessage("Payment dates should be equals")
                    .isEqualTo(agreement.getTerminationDate());

        });
    }

    private void verifyCreditSchedule(CreditScheduleDto creditScheduleDto, Account account, Agreement agreement,
                                      List<PaymentSchedule> paymentScheduleList, PaymentScheduleDto paymentScheduleDto) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(creditScheduleDto.getAccountNumber())
                    .withFailMessage("Account number should be equals")
                    .isEqualTo(account.getAccountNumber());
            softAssertions.assertThat(creditScheduleDto.getAgreementID())
                    .withFailMessage("Agreement ID should be equals")
                    .isEqualTo(agreement.getId());
            softAssertions.assertThat(creditScheduleDto.getPrincipalDebt())
                    .withFailMessage("Principal debts should be equals")
                    .isEqualTo(account.getPrincipalDebt());
            softAssertions.assertThat(creditScheduleDto.getInterestDebt())
                    .withFailMessage("Interest debts should be equals")
                    .isEqualTo(account.getInterestDebt());
            softAssertions.assertThat(creditScheduleDto.getPaymentsSchedule())
                    .withFailMessage("Payment schedules should be equals")
                    .hasSameSizeAs(paymentScheduleList)
                    .contains(paymentScheduleDto);
        });
    }
}