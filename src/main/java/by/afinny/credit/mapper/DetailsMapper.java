package by.afinny.credit.mapper;

import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.PaymentSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DetailsMapper {

    @Mapping(source = "agreement.id", target = "agreementId")
    @Mapping(source = "credit.currencyCode", target = "creditCurrencyCode")
    @Mapping(source = "paymentsSchedule.principal", target = "principal")
    @Mapping(source = "paymentsSchedule.interest", target = "interest")
    DetailsDto toDetailsDto(Agreement agreement, Account account, Credit credit, PaymentSchedule paymentsSchedule);
}
