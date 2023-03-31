package by.afinny.credit.mapper;

import by.afinny.credit.dto.ResponseOperationDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.Operation;
import by.afinny.credit.entity.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Verification of correct data generation. It will pass if the fields of the entity and dto are equal")
class OperationMapperTest {

    @InjectMocks
    private OperationMapperImpl mapper;
    private Operation operation;
    private List<ResponseOperationDto> responseOperationDto;


    @BeforeEach
    void setUp() {
        Credit credit = new Credit();
        Account account = new Account();
        OperationType operationType = new OperationType();
        account.setCredit(credit);
        operation = Operation.builder()
                .id(UUID.randomUUID())
                .sum(BigDecimal.valueOf(1000))
                .completedAt(LocalDateTime.now())
                .details("test")
                .currencyCode("test")
                .account(account)
                .type(operationType)
                .build();
        List<Operation> operations = new ArrayList<>();
        operations.add(operation);

        responseOperationDto = mapper.detailsOperationToResponseOperations(operations);
    }
    @Test
    @DisplayName("verify List<ResponseOperationDto> fields settings")
    void toResponseOperations_shouldReturnListResponseOperationDto() {
        List<ResponseOperationDto> responseOperationDtoList = mapper.detailsOperationToResponseOperations(List.of(operation));
        verifyResponseOperationDto(responseOperationDtoList.get(0));
    }

    @Test
    @DisplayName("verify ResponseOperationDto fields settings")
    void toResponseOperationDto_shouldReturnResponseOperationDto() {
        ResponseOperationDto responseOperation = mapper.detailsOperationToResponseOperation(operation);
        verifyResponseOperationDto(responseOperation);
    }

    private void verifyResponseOperationDto(ResponseOperationDto responseOperationDto) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(responseOperationDto.getCompletedAt()).isEqualTo(operation.getCompletedAt());
            softAssertions.assertThat(responseOperationDto.getSum()).isEqualTo(operation.getSum());
            softAssertions.assertThat(responseOperationDto.getDetails()).isEqualTo(operation.getDetails());
            softAssertions.assertThat(responseOperationDto.getCurrencyCode()).isEqualTo(operation.getCurrencyCode());
            softAssertions.assertThat(responseOperationDto.getAccountId()).isEqualTo(operation.getAccount().getId());
            softAssertions.assertThat(responseOperationDto.getOperationType()).isEqualTo(operation.getType().getType());
            softAssertions.assertThat(responseOperationDto.getType()).isEqualTo(operation.getAccount().getCredit().getType());
        });
    }

}