package by.afinny.credit.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentScheduleDto {

    private LocalDate paymentDate;
    private BigDecimal paymentPrincipal;
    private BigDecimal paymentInterest;
}
