package by.afinny.credit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScheduleDto {

    private String accountNumber;
    private UUID agreementID;
    private BigDecimal principalDebt;
    private BigDecimal interestDebt;

    @JsonProperty("payments")
    private List<PaymentScheduleDto> paymentsSchedule;
}