package by.afinny.credit.service;

import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    ResponseCreditOrderDto createOrder(UUID clientId, RequestCreditOrderDto requestCreditOrderDto);

    List<ResponseCreditOrderDto> getCreditOrders(UUID clientId);

    void deleteCreditOrder(UUID clientId, UUID creditOrderId);
}
