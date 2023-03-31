package by.afinny.credit.mapper;


import by.afinny.credit.dto.PaymentScheduleDto;
import by.afinny.credit.entity.PaymentSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface ScheduleMapper {

    @Mapping(source = "principal", target = "paymentPrincipal")
    @Mapping(source = "interest", target = "paymentInterest")
    PaymentScheduleDto toPaymentScheduleDto(PaymentSchedule paymentSchedule);
}
