package by.afinny.credit.unit.mapper;

import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.dto.kafka.EmployerEvent;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditOrderStatus;
import by.afinny.credit.mapper.CreditOrderMapperImpl;
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
class CreditOrderMapperTest {

    @InjectMocks
    private CreditOrderMapperImpl creditOrderMapper;

    private CreditOrder creditOrder;
    private RequestCreditOrderDto requestCreditOrderDto;

    @BeforeEach
    void setUp() {
        Product product = Product.builder()
                .id(1).build();

        creditOrder = CreditOrder.builder()
                .amount(BigDecimal.valueOf(35))
                .id(UUID.randomUUID())
                .creationDate(LocalDate.now().minusYears(3))
                .periodMonths(5)
                .status(CreditOrderStatus.APPROVED)
                .product(product).build();

        requestCreditOrderDto = RequestCreditOrderDto.builder()
                .amount(BigDecimal.valueOf(123))
                .employerIdentificationNumber("Employee ID")
                .monthlyExpenditure(BigDecimal.valueOf(1337))
                .monthlyIncome(BigDecimal.ONE)
                .periodMonths(5).build();
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void creditOrderToResponseDto_checkCorrectMappingData() {
        ResponseCreditOrderDto responseCreditOrderDto = creditOrderMapper.creditOrderToResponseDto(creditOrder);
        verifyCreditOrder(creditOrder, responseCreditOrderDto);
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void requestDtoToCreditOrder_checkCorrectMappingData() {
        CreditOrder creditOrder = creditOrderMapper.requestDtoToCreditOrder(requestCreditOrderDto);
        verifyCreditOrder(creditOrder, requestCreditOrderDto);
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void requestDtoToUpdateEmployerIdEvent_checkCorrectMappingData() {
        EmployerEvent employerEvent = creditOrderMapper.requestDtoToUpdateEmployerIdEvent(requestCreditOrderDto);
        assertSoftly(softAssertions ->
                softAssertions.assertThat(employerEvent.getEmployerIdentificationNumber())
                        .withFailMessage("EmployerIdentificationNumber should be equals")
                        .isEqualTo(requestCreditOrderDto.getEmployerIdentificationNumber())
        );
    }

    private void verifyCreditOrder(CreditOrder creditOrder, ResponseCreditOrderDto responseCreditOrderDto) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(responseCreditOrderDto.getId())
                    .withFailMessage("Id should be equals")
                    .isEqualTo(creditOrder.getId());
            softAssertions.assertThat(responseCreditOrderDto.getAmount())
                    .withFailMessage("Amount should be equals")
                    .isEqualTo(creditOrder.getAmount());
            softAssertions.assertThat(responseCreditOrderDto.getCreationDate())
                    .withFailMessage("CreationDate should be equals")
                    .isEqualTo(creditOrder.getCreationDate());
            softAssertions.assertThat(responseCreditOrderDto.getPeriodMonths())
                    .withFailMessage("PeriodMonths should be equals")
                    .isEqualTo(creditOrder.getPeriodMonths());
            softAssertions.assertThat(responseCreditOrderDto.getStatus())
                    .withFailMessage("Status should be equals")
                    .isEqualTo(creditOrder.getStatus());
            softAssertions.assertThat(responseCreditOrderDto.getProductId())
                    .withFailMessage("ProductId should be equals")
                    .isEqualTo(creditOrder.getProduct().getId());
        });
    }

    private void verifyCreditOrder(CreditOrder creditOrder, RequestCreditOrderDto requestCreditOrderDto) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(creditOrder.getAmount())
                    .withFailMessage("Amount should be equals")
                    .isEqualTo(requestCreditOrderDto.getAmount());
            softAssertions.assertThat(creditOrder.getCreationDate())
                    .withFailMessage("CreationDate should be equals")
                    .isEqualTo(requestCreditOrderDto.getCreationDate());
            softAssertions.assertThat(creditOrder.getPeriodMonths())
                    .withFailMessage("PeriodMonths should be equals")
                    .isEqualTo(requestCreditOrderDto.getPeriodMonths());
            softAssertions.assertThat(creditOrder.getEmployerIdentificationNumber())
                    .withFailMessage("EmployerIdentificationNumber should be equals")
                    .isEqualTo(requestCreditOrderDto.getEmployerIdentificationNumber());
            softAssertions.assertThat(creditOrder.getMonthlyExpenditure())
                    .withFailMessage("MonthlyExpenditure should be equals")
                    .isEqualTo(requestCreditOrderDto.getMonthlyExpenditure());
            softAssertions.assertThat(creditOrder.getMonthlyIncome())
                    .withFailMessage("MonthlyIncome should be equals")
                    .isEqualTo(requestCreditOrderDto.getMonthlyIncome());
        });
    }
}