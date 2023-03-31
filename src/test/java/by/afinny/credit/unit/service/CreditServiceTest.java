package by.afinny.credit.unit.service;

import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.dto.ResponseOperationDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Operation;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditStatus;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.mapper.CreditMapper;
import by.afinny.credit.mapper.DetailsMapper;
import by.afinny.credit.mapper.OperationMapperImpl;
import by.afinny.credit.repository.AgreementRepository;
import by.afinny.credit.repository.CreditRepository;
import by.afinny.credit.repository.OperationRepository;
import by.afinny.credit.service.impl.CreditServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class CreditServiceTest {

    @Mock
    private CreditRepository creditRepository;
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private DetailsMapper detailsMapper;
    @InjectMocks
    private CreditServiceImpl creditService;

    private Specification<Operation> specification;
    private List<ResponseOperationDto> responseOperationDtos;
    private Page<Operation> operations;
    @Mock
    private OperationMapperImpl operationMapper;

    private static CreditScheduleDto creditScheduleDto;
    private static CreditBalanceDto creditBalanceDto;
    private static DetailsDto detailsDto;
    private static List<Credit> expectedClientCredits;
    private static List<CreditDto> expectedClientCreditsDto;
    private static Optional<Credit> optionalCredit;
    private static Credit credit;
    private static Account account;
    private static Agreement agreement;
    private static PaymentSchedule paymentSchedule;

    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID CREDIT_ID = UUID.randomUUID();
    private static final UUID AGREEMENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        expectedClientCreditsDto = new ArrayList<>();
        expectedClientCredits = new ArrayList<>();
        expectedClientCredits.add(Credit.builder()
                .id(UUID.randomUUID())
                .status(CreditStatus.ACTIVE).build());
        expectedClientCredits.add(Credit.builder()
                .id(UUID.randomUUID())
                .status(CreditStatus.ACTIVE).build());

        Product product = new Product();
        product.setName("name");
        CreditOrder creditOrder = new CreditOrder();
        creditOrder.setProduct(product);
        credit = new Credit();
        credit.setCreditLimit(new BigDecimal(1000));
        credit.setInterestRate(new BigDecimal(10));
        credit.setCurrencyCode("RUB");
        credit.setCreditOrder(creditOrder);
        account = new Account();
        account.setPrincipalDebt(new BigDecimal(750));
        account.setInterestDebt(new BigDecimal(50));
        paymentSchedule = new PaymentSchedule();
        List<PaymentSchedule> paymentSchedules = new ArrayList<>();
        paymentSchedule.setPaymentDate(LocalDate.now().plusYears(5));
        paymentSchedule.setPrincipal(new BigDecimal(100));
        paymentSchedule.setInterest(new BigDecimal(20));
        paymentSchedules.add(paymentSchedule);
        account.setPaymentSchedules(paymentSchedules);
        account.setCard(Card.builder()
                                        .cardNumber("58551")
                                        .balance(BigDecimal.TEN)
                                        .id(UUID.randomUUID())
                                        .build());
        credit.setAccount(account);
        agreement = Agreement.builder()
                .id(UUID.randomUUID()).build();
        agreement.setCredit(credit);

        creditBalanceDto = new CreditBalanceDto();
        creditScheduleDto = new CreditScheduleDto();
        detailsDto = new DetailsDto();

        responseOperationDtos = List.of(ResponseOperationDto.builder()
                .operationId(UUID.randomUUID())
                .completedAt(LocalDateTime.now())
                .details("test")
                .accountId(UUID.randomUUID())
                .operationType("test")
                .currencyCode("123")
                .type("test")
                .build());
        specification = (root, query, builder) -> builder.equal(root.get("account"), account);
        operations = Page.empty(PageRequest.of(0, 4));
        optionalCredit = Optional.of(credit);
    }

    @Test
    @DisplayName("Return list of operation when program was found")
    void getDetailsOfLastPayments_shouldReturnListResponseOperationDto() {
        //ARRANGE
        when(operationRepository.findAllOperationCreditTransactions(any(Account.class)))
                .thenReturn(specification);
        when(operationRepository.findAll(specification, PageRequest.of(0, 4)))
                .thenReturn(operations);
        when(operationMapper.detailsOperationToResponseOperations(operations.stream().collect(Collectors.toList())))
                .thenReturn(responseOperationDtos);
        when(creditRepository.findByCreditOrderClientIdAndId(credit.getId(), credit.getId())).thenReturn(optionalCredit);
        //ACT
        List<ResponseOperationDto> responseOperationDtoList = creditService.getDetailsOfLastPayments(optionalCredit.get().getId(), optionalCredit.get().getId(),
                0, 4);
        //VERIFY
        assertThat(responseOperationDtoList).isEqualTo(responseOperationDtos);
    }

    @Test
    @DisplayName("If operation wasn't found then throw exception")
    void getDetailsOfLastPayments_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(operationRepository.findAllOperationCreditTransactions(any(Account.class)))
                .thenReturn(specification);
        when(operationRepository.findAll(specification, PageRequest.of(0, 4)))
                .thenReturn(operations);
        when(operationMapper.detailsOperationToResponseOperations(operations.stream().collect(Collectors.toList())))
                .thenThrow(RuntimeException.class);
        when(creditRepository.findByCreditOrderClientIdAndId(credit.getId(), credit.getId())).thenReturn(optionalCredit);
        //ACT
        ThrowableAssert.ThrowingCallable createOrderMethodInvocation = () -> creditService.getDetailsOfLastPayments(optionalCredit.get().getId(), optionalCredit.get().getId(),
                0, 4);
        //VERIFY
        assertThatThrownBy(createOrderMethodInvocation).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("If success then actual and expected amount of credits are equals")
    void getClientCurrentCredits_ifSuccess_returnCredits() {
        //ARRANGE
        when(creditRepository.findByCreditOrderClientIdAndStatus(CLIENT_ID, CreditStatus.ACTIVE))
                .thenReturn(expectedClientCredits);
        when(creditMapper.creditsToCreditDto(expectedClientCredits))
                .thenReturn(expectedClientCreditsDto);

        //ACT
        List<CreditDto> actualClientCredits = creditService.getClientCreditsWithActiveStatus(CLIENT_ID);

        //VERIFY
        verify(creditRepository).findByCreditOrderClientIdAndStatus(CLIENT_ID, CreditStatus.ACTIVE);
        assertThat(actualClientCredits).isEqualTo(expectedClientCreditsDto);
    }

    @Test
    @DisplayName("If not success then throw Runtime Exception")
    void getClientCurrentCredits_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(creditRepository.findByCreditOrderClientIdAndStatus(CLIENT_ID, CreditStatus.ACTIVE))
                .thenThrow(RuntimeException.class);

        //ACT
        ThrowingCallable getClientCurrentCreditsMethodInvocation = () -> creditService
                .getClientCreditsWithActiveStatus(CLIENT_ID);

        //VERIFY
        assertThatThrownBy(getClientCurrentCreditsMethodInvocation).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("If credit with incoming id was found then return information about credit")
    void getCreditId_ifCreditFound_thenReturnDto() {
        //ARRANGE
        when(creditRepository.findByCreditOrderClientIdAndId(CLIENT_ID, CREDIT_ID)).thenReturn(Optional.of(credit));
        when(creditMapper.toCreditBalanceDto(credit.getCreditOrder().getProduct(), credit, credit.getAgreement(),
                credit.getAccount(), credit.getAccount().getCard(), paymentSchedule)).thenReturn(creditBalanceDto);

        //ACT
        CreditBalanceDto result = creditService.getCreditBalance(CLIENT_ID, CREDIT_ID);

        //VERIFY
        assertThat(result)
                .isNotNull()
                .isEqualTo(creditBalanceDto);
    }

    @Test
    @DisplayName("If credit with incoming id wasn't found throw RuntimeException")
    void getCreditId_ifCreditNotFound_thenThrow() {
        //ARRANGE
        when(creditRepository.findByCreditOrderClientIdAndId(CLIENT_ID, CREDIT_ID)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable getCreditBalanceMethodInvocation = () -> creditService.getCreditBalance(CLIENT_ID, CREDIT_ID);

        //VERIFY
        assertThatThrownBy(getCreditBalanceMethodInvocation).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If credit payment schedule with incoming id was found then return information about credit")
    void getCreditPaymentSchedule_ifCreditFound_thenReturnDto() {
        //ARRANGE
        when(creditRepository.findByCreditOrderClientIdAndId(CLIENT_ID, CREDIT_ID)).thenReturn(Optional.of(credit));
        when(creditMapper.toCreditScheduleDto(credit.getAgreement(), credit.getAccount(), credit.getAccount().getPaymentSchedules()))
                .thenReturn(creditScheduleDto);

        //ACT
        CreditScheduleDto result = creditService.getPaymentSchedule(CLIENT_ID, CREDIT_ID);

        //VERIFY
        assertThat(result)
                .isNotNull()
                .isEqualTo(creditScheduleDto);
    }

    @Test
    @DisplayName("If credit payment schedule with incoming id wasn't found throw RuntimeException")
    void getCreditPaymentSchedule_ifCreditNotFound_thenThrow() {
        //ARRANGE
        when(creditRepository.findByCreditOrderClientIdAndId(CLIENT_ID, CREDIT_ID)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable getPaymentScheduleMethodInvocation = () -> creditService.getPaymentSchedule(CLIENT_ID, CREDIT_ID);

        //VERIFY
        assertThatThrownBy(getPaymentScheduleMethodInvocation).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If agreement with incoming id was found then return information about credit")
    void getCreditDetails_ifCreditFound_thenReturnDto() {
        //ARRANGE
        when(agreementRepository.findByCreditCreditOrderClientIdAndId(CLIENT_ID, AGREEMENT_ID)).thenReturn(Optional.of(agreement));
        when(detailsMapper.toDetailsDto(agreement, account, credit, paymentSchedule))
                .thenReturn(detailsDto);

        //ACT
        DetailsDto result = creditService.getDetailsForPayment(CLIENT_ID, AGREEMENT_ID);

        //VERIFY
        assertThat(result)
                .isNotNull()
                .isEqualTo(detailsDto);
    }

    @Test
    @DisplayName("If agreement with incoming id wasn't found throw RuntimeException")
    void getCreditDetails_ifCreditNotFound_thenThrow() {
        //ARRANGE
        when(agreementRepository.findByCreditCreditOrderClientIdAndId(CLIENT_ID, AGREEMENT_ID)).thenReturn(Optional.empty());

        //ACT
        ThrowingCallable getDetailsForPaymentMethodInvocation = () -> creditService.getDetailsForPayment(CLIENT_ID, AGREEMENT_ID);

        //VERIFY
        assertThatThrownBy(getDetailsForPaymentMethodInvocation).isInstanceOf(EntityNotFoundException.class);
    }
}