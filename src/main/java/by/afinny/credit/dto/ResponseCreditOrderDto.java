package by.afinny.credit.dto;

import by.afinny.credit.entity.constant.CreditOrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCreditOrderDto {

    private UUID id;
    private Integer productId;
    private String productName;
    private CreditOrderStatus status;
    private BigDecimal amount;
    private Integer periodMonths;
    private LocalDate creationDate;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal principalDebt;
    private BigDecimal interestDebt;
    private LocalDate paymentDate;
}
