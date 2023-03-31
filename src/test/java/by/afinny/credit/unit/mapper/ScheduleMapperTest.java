package by.afinny.credit.unit.mapper;

import by.afinny.credit.dto.PaymentScheduleDto;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.mapper.ScheduleMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Verification of correct data generation. It will pass if the fields of the entity and dto are equal")
class ScheduleMapperTest {

    @InjectMocks
    private ScheduleMapperImpl scheduleMapper;

    private PaymentSchedule paymentSchedule;
    private PaymentScheduleDto paymentScheduleDto;

    @BeforeEach
    void setUp() {
        paymentSchedule = PaymentSchedule.builder()
                .paymentDate(LocalDate.of(2022, 4, 20))
                .principal(BigDecimal.valueOf(500))
                .interest(BigDecimal.valueOf(10)).build();

        paymentScheduleDto = scheduleMapper.toPaymentScheduleDto(paymentSchedule);
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void toCreditBalanceDto_checkCorrectMappingData() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(paymentScheduleDto.getPaymentDate())
                    .withFailMessage("Payment dates should be equals")
                    .isEqualTo(paymentSchedule.getPaymentDate());
            softAssertions.assertThat(paymentScheduleDto.getPaymentInterest())
                    .withFailMessage("Payment interests should be equals")
                    .isEqualTo(paymentSchedule.getInterest());
            softAssertions.assertThat(paymentScheduleDto.getPaymentPrincipal())
                    .withFailMessage("Payment principals  should be equals")
                    .isEqualTo(paymentSchedule.getPrincipal());
        });
    }
}