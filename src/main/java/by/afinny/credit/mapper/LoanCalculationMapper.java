package by.afinny.credit.mapper;

import by.afinny.credit.dto.LoanCalculationDto;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface LoanCalculationMapper {
    LoanCalculationDto toLoanCalculationDto(BigDecimal principal,
                                            BigDecimal interest,
                                            BigDecimal principalDebt,
                                            BigDecimal interestDebt,
                                            LocalDate paymentDate);
}
