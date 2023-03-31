package by.afinny.credit.service.impl;

import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.dto.kafka.EmployerEvent;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditOrderStatus;
import by.afinny.credit.exception.CreditOrderStatusException;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.mapper.CreditOrderMapper;
import by.afinny.credit.repository.CreditOrderRepository;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.service.CalculationService;
import by.afinny.credit.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CreditOrderRepository creditOrderRepository;
    private final ProductRepository productRepository;
    private final CalculationService calculationService;
    private final CreditOrderMapper creditOrderMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseCreditOrderDto createOrder(UUID clientId, RequestCreditOrderDto request) {
        log.info("createOrder() invoked");
        CreditOrder creditOrder = getPreparedCreditOrder(request, clientId);
        if (creditOrder.getProduct().getAutoProcessing()) {
            double coefficient = (creditOrder.getMonthlyIncome().doubleValue() - creditOrder.getMonthlyExpenditure().doubleValue()) * creditOrder.getPeriodMonths() / creditOrder.getAmount().doubleValue() - 1;
            if (coefficient < 0.2)
                creditOrder.setStatus(CreditOrderStatus.REJECT);
            else
                creditOrder.setStatus(CreditOrderStatus.APPROVED);
        } else {
            creditOrder.setStatus(CreditOrderStatus.PENDING);
            sendToKafka(request.getEmployerIdentificationNumber(), clientId);
        }
        creditOrder = creditOrderRepository.save(creditOrder);
        log.info("creditOrder was saved");

        return creditOrder.getStatus() == CreditOrderStatus.APPROVED ?
                creditOrderMapper.creditOrderAndLoanCalculationDtoToResponseDto(creditOrder, calculationService.loanCalculation(creditOrder)) :
                creditOrderMapper.creditOrderToResponseDto(creditOrder);
    }

    @Override
    public List<ResponseCreditOrderDto> getCreditOrders(UUID clientId) {
        log.info("getCreateOrder() invoked");

        List<CreditOrder> creditOrders = creditOrderRepository.findAllByClientId(clientId);
        List<ResponseCreditOrderDto> responseCreditOrders = creditOrders.stream()
                .map(creditOrderMapper::creditOrderToResponseDto)
                .collect(Collectors.toList());
        return responseCreditOrders;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCreditOrder(UUID clientId, UUID creditOrderId) {
        log.info("deleteCreditOrder() method invoke with transferId: {}", creditOrderId);
        CreditOrder foundCreditOrder = getCreditOrderById(clientId, creditOrderId);
        if (!foundCreditOrder.getStatus().equals(CreditOrderStatus.PENDING)) {
            throw new CreditOrderStatusException("Credit order with this " + creditOrderId + " has a " +
                    foundCreditOrder.getStatus() + ", you can delete a credit order with the pending status");
        }
        creditOrderRepository.delete(foundCreditOrder);
    }

    private void sendToKafka(String employerId, UUID clientId) {
        EmployerEvent event = new EmployerEvent();
        event.setClientId(clientId);
        event.setEmployerIdentificationNumber(employerId);
        log.info("publishing event...");
        eventPublisher.publishEvent(event);
    }

    private CreditOrder getPreparedCreditOrder(RequestCreditOrderDto request, UUID clientId) {
        CreditOrder creditOrder = creditOrderMapper.requestDtoToCreditOrder(request);
        creditOrder.setClientId(clientId);
        creditOrder.setProduct(getProduct(request.getProductId()));
        return creditOrder;
    }

    private Product getProduct(Integer productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("product with id " + productId + " wasn't found"));
    }

    private CreditOrder getCreditOrderById(UUID clientId, UUID creditOrderId) {
        return creditOrderRepository.findByClientIdAndId(clientId, creditOrderId).orElseThrow(
                () -> new EntityNotFoundException("credit order with id: " + creditOrderId + " not found"));
    }
}
