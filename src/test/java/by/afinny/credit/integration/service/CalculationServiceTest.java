package by.afinny.credit.integration.service;

import by.afinny.credit.dto.LoanCalculationDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CalculationMode;
import by.afinny.credit.entity.constant.CreditOrderStatus;
import by.afinny.credit.entity.constant.CreditType;
import by.afinny.credit.integration.config.annotation.TestWithPostgresContainer;
import by.afinny.credit.repository.AccountRepository;
import by.afinny.credit.repository.CreditOrderRepository;
import by.afinny.credit.repository.CreditRepository;
import by.afinny.credit.repository.PaymentScheduleRepository;
import by.afinny.credit.service.impl.CalculationServiceImpl;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@TestWithPostgresContainer
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration test for credit ")
class CalculationServiceTest {


    @Autowired
    private CalculationServiceImpl calculationService;
    @Autowired
    private CreditOrderRepository creditOrderRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private PaymentScheduleRepository paymentScheduleRepository;

    private CreditOrder creditOrder;
    private Product product;
    private LoanCalculationDto loanCalculationDto;
    private final UUID TEST_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {

        product = Product.builder()
                .name("name")
                .minSum(BigDecimal.valueOf(100000).movePointLeft(4))
                .maxSum(BigDecimal.valueOf(300000).movePointLeft(4))
                .currencyCode("EUR")
                .minInterestRate(BigDecimal.valueOf(30000).movePointLeft(4))
                .maxInterestRate(BigDecimal.valueOf(70000).movePointLeft(4))
                .needGuarantees(false)
                .deliveryInCash(true)
                .earlyRepayment(true)
                .needIncomeDetails(true)
                .minPeriodMonths(2)
                .maxPeriodMonths(36)
                .isActive(true)
                .details("details")
                .calculationMode(CalculationMode.DIFFERENTIATED)
                .gracePeriodMonths(2)
                .rateIsAdjustable(true)
                .rateBase("base")
                .rateFixPart(BigDecimal.valueOf(100000).movePointLeft(4))
                .autoProcessing(true)
                .typeCredit(CreditType.CONSUMER)
                .build();
        creditOrder = CreditOrder.builder()
                .number("1234567")
                .clientId(TEST_ID)
                .status(CreditOrderStatus.APPROVED)
                .amount(BigDecimal.valueOf(200000).movePointLeft(4))
                .periodMonths(6)
                .creationDate(LocalDate.of(2023, 2, 3))
                .monthlyIncome(BigDecimal.valueOf(100000).movePointLeft(4))
                .employerIdentificationNumber("number")
                .build();
        loanCalculationDto = LoanCalculationDto.builder()
                .principal(BigDecimal.valueOf(3.33))
                .interest(BigDecimal.valueOf(0.17))
                .principalDebt(BigDecimal.valueOf(200000).movePointLeft(4))
                .interestDebt(BigDecimal.valueOf(1.02))
                .paymentDate(LocalDate.of(2023, 3, 3))
                .build();
    }

    @Test
    @DisplayName("If successfully then save Account, Credit, PaymentSchedule and return LoanCalculationDto")
    void methodLoanCalculation_success_thenReturnLoanCalculationDto_saveEntities() throws Exception {
        //ARRANGE
        creditOrder.setProduct(product);
        creditOrderRepository.save(creditOrder);

        //ACT
        LoanCalculationDto result = calculationService.loanCalculation(creditOrder);

        //VERIFY
        verifyBody(asJsonString(result), asJsonString(loanCalculationDto));
        verifyBody(asJsonString(creditRepository.findAll().get(0).getCreditOrder()), asJsonString(creditOrder));
        Account accountFromDb = accountRepository.findAll().get(0);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(accountFromDb.getOpeningDate()).isEqualTo(creditOrder.getCreationDate());
            softAssertions.assertThat(accountFromDb.getCurrencyCode()).isEqualTo(product.getCurrencyCode());
            softAssertions.assertThat(accountFromDb.getPrincipalDebt()).isEqualByComparingTo(loanCalculationDto.getPrincipalDebt());
            softAssertions.assertThat(accountFromDb.getInterestDebt()).isEqualByComparingTo(loanCalculationDto.getInterestDebt());
        });
        assertThat(paymentScheduleRepository.findAll().get(0)).isNotNull();
    }

    private String asJsonString(Object obj) throws com.fasterxml.jackson.core.JsonProcessingException {
        return new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(obj);
    }

    private void verifyBody(String actualBody, String expectedBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }

}