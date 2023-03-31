package by.afinny.credit.mapper;

import by.afinny.credit.dto.LoanCalculationDto;
import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.dto.kafka.EmployerEvent;
import by.afinny.credit.entity.CreditOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CreditOrderMapper {

    CreditOrder requestDtoToCreditOrder(RequestCreditOrderDto dto);

    EmployerEvent requestDtoToUpdateEmployerIdEvent(RequestCreditOrderDto dto);

    @Mapping(target = "productId", source = "creditOrder.product.id")
    @Mapping(target = "productName", source = "creditOrder.product.name")
    ResponseCreditOrderDto creditOrderToResponseDto(CreditOrder creditOrder);

    @Mapping(target = "productId", source = "creditOrder.product.id")
    @Mapping(target = "productName", source = "creditOrder.product.name")
    ResponseCreditOrderDto creditOrderAndLoanCalculationDtoToResponseDto(CreditOrder creditOrder, LoanCalculationDto loanCalculationDto);
}
