package by.afinny.credit.unit.mapper;

import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditType;
import by.afinny.credit.mapper.DetailsMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Verification of correct data generation. It will pass if the fields of the entity and dto are equal")
class DetailsMapperTest {

    @InjectMocks
    private DetailsMapperImpl detailsMapper;

    private Credit credit;
    private Account account;
    private Agreement agreement;
    private PaymentSchedule paymentSchedule;

    private DetailsDto detailsDto;

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
                .creditLimit(new BigDecimal(5000.00))
                .interestRate(new BigDecimal(5.00))
                .gracePeriodMonths(5)
                .type(CreditType.CONSUMER)
                .build();

        agreement = new Agreement();
        agreement.setAgreementDate(LocalDate.now().plusYears(1));
        agreement.setId(UUID.randomUUID());
        agreement.setTerminationDate(LocalDate.now().plusYears(1));

        account = new Account();
        account.setPrincipalDebt(new BigDecimal(10.00));
        account.setInterestDebt(new BigDecimal(15.00));

        agreement = Agreement.builder()
                .id(UUID.randomUUID()).build();

        paymentSchedule = new PaymentSchedule();
        paymentSchedule.setPrincipal(new BigDecimal(500.00));
        paymentSchedule.setInterest(new BigDecimal(10.00));

        detailsDto = detailsMapper.toDetailsDto(agreement, account, credit, paymentSchedule);
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void toDetailsDto() {
        verifyCreditBalance(detailsDto, agreement, credit, account, paymentSchedule);
    }

    private void verifyCreditBalance(DetailsDto detailsDto, Agreement agreement, Credit credit, Account account, PaymentSchedule paymentSchedule) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(detailsDto.getAgreementId())
                    .withFailMessage("Agreement id should be equals")
                    .isEqualTo(agreement.getId());
            softAssertions.assertThat(detailsDto.getCreditCurrencyCode())
                    .withFailMessage("Currency code should be equals")
                    .isEqualTo(credit.getCurrencyCode());
            softAssertions.assertThat(detailsDto.getPrincipalDebt())
                    .withFailMessage("Principal debts should be equals")
                    .isEqualTo(account.getPrincipalDebt());
            softAssertions.assertThat(detailsDto.getInterestDebt())
                    .withFailMessage("Interest debts should be equals")
                    .isEqualTo(account.getInterestDebt());
            softAssertions.assertThat(detailsDto.getPrincipal())
                    .withFailMessage("Principals should be equals")
                    .isEqualTo(paymentSchedule.getPrincipal());
            softAssertions.assertThat(detailsDto.getInterest())
                    .withFailMessage("Interests should be equals")
                    .isEqualTo(paymentSchedule.getInterest());
        });
    }
}