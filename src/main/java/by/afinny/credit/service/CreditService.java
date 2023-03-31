package by.afinny.credit.service;

import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.dto.ResponseOperationDto;

import java.util.List;
import java.util.UUID;

public interface CreditService {

    List<CreditDto> getClientCreditsWithActiveStatus(UUID clientId);

    CreditBalanceDto getCreditBalance(UUID clientId, UUID creditId);

    CreditScheduleDto getPaymentSchedule(UUID clientId, UUID creditId);

    DetailsDto getDetailsForPayment(UUID clientId, UUID agreementId);

    List<ResponseOperationDto> getDetailsOfLastPayments(UUID clientId, UUID creditId, Integer pageNumber, Integer pageSize);
}
