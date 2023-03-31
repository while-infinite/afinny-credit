package by.afinny.credit.service.impl;

import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.dto.ResponseOperationDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.Operation;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditStatus;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.mapper.CreditMapper;
import by.afinny.credit.mapper.DetailsMapper;
import by.afinny.credit.mapper.OperationMapper;
import by.afinny.credit.repository.AgreementRepository;
import by.afinny.credit.repository.CreditRepository;
import by.afinny.credit.repository.OperationRepository;
import by.afinny.credit.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final AgreementRepository agreementRepository;
    private final OperationRepository operationRepository;
    private final CreditMapper creditMapper;

    private final DetailsMapper detailsMapper;
    private final OperationMapper operationMapper;

    @Override
    public List<CreditDto> getClientCreditsWithActiveStatus(UUID clientId) {
        log.info("getClientCreditsWithActiveStatus() invoked");
        List<Credit> clientCreditsWithActiveStatus = creditRepository
                .findByCreditOrderClientIdAndStatus(clientId, CreditStatus.ACTIVE);
        return creditMapper.creditsToCreditDto(clientCreditsWithActiveStatus);
    }

    public CreditBalanceDto getCreditBalance(UUID clientId, UUID creditId) {
        log.info("getCreditBalance() method invoke");

        Credit credit = creditRepository.findByCreditOrderClientIdAndId(clientId, creditId).orElseThrow(
                () -> new EntityNotFoundException("credit with id " + creditId + " wasn't found"));
        Product product = credit.getCreditOrder().getProduct();
        Account account = credit.getAccount();
        Agreement agreement = credit.getAgreement();
        Card card = account.getCard();
        List<PaymentSchedule> paymentSchedules = account.getPaymentSchedules();

        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
        PaymentSchedule payment = paymentSchedules.stream()
                .filter(ps -> ps.getPaymentDate().isAfter(yesterday))
                .min(Comparator.comparing(PaymentSchedule::getPaymentDate)).orElse(null);
        return creditMapper.toCreditBalanceDto(product, credit, agreement, account, card, payment);
    }

    @Override
    public CreditScheduleDto getPaymentSchedule(UUID clientId, UUID creditId) {
        log.info("getCreditBalance() method with creditId: {} invoke", creditId);

        Credit credit = creditRepository.findByCreditOrderClientIdAndId(clientId, creditId).orElseThrow(
                () -> new EntityNotFoundException("credit with id " + creditId + " wasn't found"));
        Agreement agreement = credit.getAgreement();
        Account account = credit.getAccount();

        return creditMapper.toCreditScheduleDto(agreement, account, account.getPaymentSchedules());
    }

    @Override
    public DetailsDto getDetailsForPayment(UUID clientId, UUID agreementId) {
        log.info("getRequisitesForPayment() method invoke");

        Agreement agreement = agreementRepository.findByCreditCreditOrderClientIdAndId(clientId, agreementId).orElseThrow(
                () -> new EntityNotFoundException("agreement with id " + agreementId + " wasn't found"));
        Credit credit = agreement.getCredit();
        Account account = credit.getAccount();
        PaymentSchedule payment = getNearestPayment(account);
        return detailsMapper.toDetailsDto(agreement, account, credit, payment);
    }

    @Override
    public List<ResponseOperationDto> getDetailsOfLastPayments(UUID clientId, UUID creditId, Integer pageNumber, Integer pageSize) {
        log.info("getDetailsOfLastPayments() method invoke");
        if (pageSize < 1) {
            pageSize = 4;
        }
        Credit credit = creditRepository.findByCreditOrderClientIdAndId(clientId, creditId).orElseThrow(
                () -> new EntityNotFoundException("credit with id " + creditId + " wasn't found"));
        Account account = credit.getAccount();
        Specification<Operation> spec = operationRepository.findAllOperationCreditTransactions(account);
        Page<Operation> operations = operationRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
        return operationMapper.detailsOperationToResponseOperations(operations.stream().sorted().collect(Collectors.toList()));
    }

    private PaymentSchedule getNearestPayment(Account account) {
        List<PaymentSchedule> paymentSchedules = account.getPaymentSchedules();

        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
        return paymentSchedules.stream()
                .filter(ps -> ps.getPaymentDate().isAfter(yesterday))
                .min(Comparator.comparing(PaymentSchedule::getPaymentDate)).orElse(null);
    }
}
