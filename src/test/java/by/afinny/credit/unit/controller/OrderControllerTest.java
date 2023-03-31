package by.afinny.credit.unit.controller;

import by.afinny.credit.controller.OrderController;
import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.entity.constant.CreditOrderStatus;
import by.afinny.credit.exception.EntityNotFoundException;
import by.afinny.credit.exception.handler.ExceptionHandlerController;
import by.afinny.credit.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;

    private List<ResponseCreditOrderDto> responseList;
    private RequestCreditOrderDto requestCreditOrderDto;
    private ResponseCreditOrderDto responseCreditOrderDto;
    private final UUID CLIENT_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();
    private final Integer productId = 1;

    @BeforeEach
    void setUp() {
        requestCreditOrderDto = RequestCreditOrderDto.builder()
                .productId(productId)
                .amount(new BigDecimal(1))
                .periodMonths(3)
                .monthlyExpenditure(new BigDecimal(1))
                .monthlyIncome(new BigDecimal(1))
                .employerIdentificationNumber("test_employer_id").build();

        responseCreditOrderDto = ResponseCreditOrderDto.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .productName("ffff")
                .status(CreditOrderStatus.PENDING)
                .periodMonths(3).build();

        responseList = List.of(responseCreditOrderDto);
    }

    @Test
    @DisplayName("If credit order was successfully created then return status OK")
    void createOrder_ifSuccessfullyCreated_then200_OK() throws Exception {
        //ARRANGE
        when(orderService.createOrder(eq(CLIENT_ID), any(RequestCreditOrderDto.class)))
                .thenReturn(responseCreditOrderDto);

        //ACT
        ResultActions perform = mockMvc.perform(
                post(OrderController.URL_CREDIT_ORDERS + OrderController.URL_NEW)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(OrderController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID))
                        .content(asJsonString(requestCreditOrderDto)));

        //VERIFY
        perform.andExpect(status().isOk());
        MvcResult result = perform.andReturn();
        verifyClientIdRequestParameter(result);
        verifyBody(asJsonString(requestCreditOrderDto), result.getRequest().getContentAsString());
        verifyBody(asJsonString(responseCreditOrderDto), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If credit order wasn't successfully created then return status INTERNAL_SERVER_ERROR")
    void createOrder_ifNotCreated_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(orderService.createOrder(any(UUID.class), any(RequestCreditOrderDto.class)))
                .thenThrow(NoSuchElementException.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                .setControllerAdvice(ExceptionHandlerController.class).build();

        //ACT
        ResultActions perform = mockMvc.perform(
                post(OrderController.URL_CREDIT_ORDERS + OrderController.URL_NEW)
                        .contentType("application/json")
                        .param(OrderController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID))
                        .content(asJsonString(requestCreditOrderDto)));

        //VERIFY
        perform.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If credit order was successfully found then return status OK")
    void getClientId_ifSuccessfullyFoundCreditOrder_then200_OK() throws Exception {
        //ARRANGE
        when(orderService.getCreditOrders(any(UUID.class))).thenReturn(responseList);

        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(OrderController.URL_CREDIT_ORDERS)
                        .param(OrderController.PARAM_CLIENT_ID, CLIENT_ID.toString()));

        //VERIFY
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        verifyClientIdRequestParameter(result);
        verifyBody(asJsonString(responseList), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If credit order wasn't found then return status OK")
    void getClientId_ifNotFoundCreditOrder_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(orderService.getCreditOrders(any(UUID.class))).thenThrow(new RuntimeException());

        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(OrderController.URL_CREDIT_ORDERS)
                        .param(OrderController.PARAM_CLIENT_ID, CLIENT_ID.toString()));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If credit order was successfully delete then return status No_Content")
    void deleteCreditOrder_ifSuccessfullyDeleted_then204_NO_CONTENT() throws Exception {
        //ARRANGE
        ArgumentCaptor<UUID> clientIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> operationIdCaptor = ArgumentCaptor.forClass(UUID.class);

        //ACT
        ResultActions perform = mockMvc.perform(
                delete(OrderController.URL_CREDIT_ORDERS
                        + OrderController.PARAM_CREDIT_ORDER_ID, ORDER_ID)
                        .param(OrderController.PARAM_CLIENT_ID, CLIENT_ID.toString()));

        //VERIFY
        perform.andExpect(status().isNoContent());
        verify(orderService, times(1)).deleteCreditOrder(clientIdCaptor.capture(), operationIdCaptor.capture());
        assertThat(CLIENT_ID).isEqualTo(clientIdCaptor.getValue());
        assertThat(ORDER_ID).isEqualTo(operationIdCaptor.getValue());
    }

    @Test
    @DisplayName("If credit order wasn't successfully delete then return status BAD_REQUEST")
    void deleteCreditOrder_ifNotDeleted_then400_BAD_REQUEST() throws Exception {
        //ARRANGE
        doThrow(EntityNotFoundException.class).when(orderService).deleteCreditOrder(any(UUID.class), any(UUID.class));

        //ACT
        ResultActions perform = mockMvc.perform(
                delete(OrderController.URL_CREDIT_ORDERS
                        + OrderController.PARAM_CREDIT_ORDER_ID, String.valueOf(ORDER_ID))
                        .param(OrderController.PARAM_CLIENT_ID, CLIENT_ID.toString()));

        //VERIFY
        perform.andExpect(status().isBadRequest());
    }

    private String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private void verifyClientIdRequestParameter(MvcResult result) {
        assertThat(result.getRequest().getParameter(OrderController.PARAM_CLIENT_ID)).isEqualTo(CLIENT_ID.toString());
    }

    private void verifyBody(String expectedBody, String actualBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}