package by.afinny.credit.unit.service;

import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.dto.kafka.EmployerEvent;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CreditOrderStatus;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.mapper.CreditOrderMapper;
import by.afinny.credit.repository.CreditOrderRepository;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.service.impl.OrderServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static by.afinny.credit.entity.constant.CreditOrderStatus.APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class OrderServiceImplTest {

    @Mock
    private CreditOrderRepository creditOrderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CreditOrderMapper creditOrderMapper;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private OrderServiceImpl orderService;

    private static RequestCreditOrderDto requestCreditOrderDto;
    private static Product product;
    private static CreditOrder creditOrder;
    private static List<CreditOrder> creditOrders;
    private static ResponseCreditOrderDto responseCreditOrderDto;
    private static List<ResponseCreditOrderDto> list;

    private final static UUID CLIENT_ID = UUID.randomUUID();
    private final static UUID ORDER_ID = UUID.randomUUID();

    @BeforeAll
    static void setUp() {
        Integer productId = 1;
        product = Product.builder()
                .id(productId)
                .minPeriodMonths(6)
                .maxPeriodMonths(12)
                .autoProcessing(false)
                .build();

        requestCreditOrderDto = RequestCreditOrderDto.builder()
                .productId(productId)
                .amount(new BigDecimal(1))
                .periodMonths(10)
                .monthlyExpenditure(new BigDecimal(1))
                .monthlyIncome(new BigDecimal(1))
                .employerIdentificationNumber("test_employer_id").build();

        creditOrder = CreditOrder.builder()
                .id(UUID.randomUUID())
                .status(CreditOrderStatus.PENDING)
                .clientId(CLIENT_ID)
                .product(product).build();

        creditOrders = new ArrayList<>();
        creditOrders.add(creditOrder);

        responseCreditOrderDto = ResponseCreditOrderDto.builder()
                .id(UUID.randomUUID())
                .productId(1)
                .productName("FFF")
                .status(APPROVED)
                .amount(new BigDecimal(1))
                .periodMonths(1)
                .creationDate(LocalDate.of(2022, 4, 20))
                .build();
        list = new ArrayList<>();
        list.add(responseCreditOrderDto);
    }

    @Test
    @DisplayName("If product with incoming id was found then save")
    void createOrder_ifProductFound_thenSave() {
        //ARRANGE
        ArgumentCaptor<EmployerEvent> employerEventArgumentCaptor = ArgumentCaptor.forClass(EmployerEvent.class);

        when(productRepository.findById(requestCreditOrderDto.getProductId())).thenReturn(Optional.of(product));
        when(creditOrderMapper.requestDtoToCreditOrder(requestCreditOrderDto)).thenReturn(creditOrder);
        when(creditOrderRepository.save(creditOrder)).thenReturn(creditOrder);
        when(creditOrderMapper.creditOrderToResponseDto(creditOrder)).thenReturn(responseCreditOrderDto);

        //ACT
        ResponseCreditOrderDto result = orderService.createOrder(CLIENT_ID, requestCreditOrderDto);

        //VERIFY
        //CreditOrder preparedCreditOrder = creditOrderArgumentCaptor.getValue();
        verify(publisher).publishEvent(employerEventArgumentCaptor.capture());
        verifyEmployerEvent(employerEventArgumentCaptor.getValue());
        verifyPreparedCreditOrder(creditOrder);
        verify(creditOrderRepository).save(creditOrder);
        assertThat(result).isEqualTo(responseCreditOrderDto);
    }

    @Test
    @DisplayName("If product with incoming id wasn't found then throw NoSuchElementException")
    void createOrder_ifProductNotFound_thenThrow() {
        //ARRANGE
        when(productRepository.findById(requestCreditOrderDto.getProductId())).thenReturn(Optional.empty());
        when(creditOrderMapper.requestDtoToCreditOrder(requestCreditOrderDto)).thenReturn(creditOrder);

        //ACT
        ThrowingCallable createOrderMethodInvocation = () -> orderService.createOrder(CLIENT_ID, requestCreditOrderDto);

        //VERIFY
        assertThatThrownBy(createOrderMethodInvocation).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If credit orders with incoming client id was found then return credit orders")
    void getClientId_ifCreditOrdersFound_thenReturn() {
        //ARRANGE
        when(creditOrderRepository.findAllByClientId(CLIENT_ID)).thenReturn(creditOrders);
        when(creditOrderMapper.creditOrderToResponseDto(creditOrder)).thenReturn(responseCreditOrderDto);

        //ACT
        List<ResponseCreditOrderDto> result = orderService.getCreditOrders(CLIENT_ID);

        //VERIFY
        assertThat(result).isNotNull();
        verifyListResponseCreditOrderDto(list, result);
    }

    @Test
    @DisplayName("If credit orders with incoming client id was found then return credit orders")
    void getClientId_ifCreditOrdersNotFound_thenThrow() {
        //ARRANGE
        when(creditOrderRepository.findAllByClientId(CLIENT_ID)).thenThrow(RuntimeException.class);

        //ACT
        ThrowingCallable getCreditOrdersMethodInvocation = () -> orderService.getCreditOrders(CLIENT_ID);

        //VERIFY
        assertThatThrownBy(getCreditOrdersMethodInvocation).isInstanceOf(RuntimeException.class);
    }
    @Test
    @DisplayName("If credit order  successfully delete then return No Content")
    void deleteCreditOrder_shouldReturnNoContent() {
        //ARRANGE
        when(creditOrderRepository.findByClientIdAndId(CLIENT_ID, ORDER_ID)).thenReturn(Optional.of(creditOrder));

        //ACT
        orderService.deleteCreditOrder(CLIENT_ID, ORDER_ID)
        ;

        //VERIFY
        verify(creditOrderRepository, times(1)).delete(creditOrder);
    }

    @Test
    @DisplayName("If not success delete then throw Runtime Exception")
    void deleteCreditOrder_ifNotSuccess_thenThrow() {
        //ARRANGE
        when(creditOrderRepository.findByClientIdAndId(CLIENT_ID, ORDER_ID)).thenThrow(javax.persistence.EntityNotFoundException.class);

        //ACT
        ThrowingCallable deleteCreditOrderMethodInvocation = () ->
                orderService.deleteCreditOrder(CLIENT_ID, ORDER_ID);

        //VERIFY
        AssertionsForClassTypes.assertThatThrownBy(deleteCreditOrderMethodInvocation).isInstanceOf(javax.persistence.EntityNotFoundException.class);
        verify(creditOrderRepository, never()).delete(creditOrder);
    }

    private void verifyEmployerEvent(EmployerEvent event) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(event.getClientId())
                    .withFailMessage("ClientId should be equals")
                    .isEqualTo(CLIENT_ID);
            softAssertions.assertThat(event.getEmployerIdentificationNumber())
                    .withFailMessage("EmployerIdentificationNumber should be equals")
                    .isEqualTo(requestCreditOrderDto.getEmployerIdentificationNumber());
        });
    }

    private void verifyPreparedCreditOrder(CreditOrder preparedCreditOrder) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(preparedCreditOrder)
                    .withFailMessage("Credit order must be created")
                    .isNotNull();
            softAssertions.assertThat(preparedCreditOrder.getStatus())
                    .withFailMessage("Status must be " + CreditOrderStatus.PENDING + " instead of " + preparedCreditOrder.getStatus())
                    .isEqualTo(CreditOrderStatus.PENDING);
            softAssertions.assertThat(preparedCreditOrder.getId())
                    .withFailMessage("Credit order id must be generated")
                    .isNotNull();
        });
    }

    private void verifyListResponseCreditOrderDto(List<ResponseCreditOrderDto> expectedList, List<ResponseCreditOrderDto> actualList) {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getId).collect(Collectors.toList()))
                    .withFailMessage("Ids should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getId).collect(Collectors.toList()));
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getProductId).collect(Collectors.toList()))
                    .withFailMessage("Product ids should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getProductId).collect(Collectors.toList()));
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getProductName).collect(Collectors.toList()))
                    .withFailMessage("Product name should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getProductName).collect(Collectors.toList()));
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getStatus).collect(Collectors.toList()))
                    .withFailMessage("Statuses should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getStatus).collect(Collectors.toList()));
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getAmount).collect(Collectors.toList()))
                    .withFailMessage("Amounts should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getAmount).collect(Collectors.toList()));
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getPeriodMonths).collect(Collectors.toList()))
                    .withFailMessage("Periods months should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getPeriodMonths).collect(Collectors.toList()));
            softAssertions.assertThat(actualList.stream().map(ResponseCreditOrderDto::getCreationDate).collect(Collectors.toList()))
                    .withFailMessage("Creation dates should be equals")
                    .isEqualTo(list.stream().map(ResponseCreditOrderDto::getCreationDate).collect(Collectors.toList()));
        });
    }
}
