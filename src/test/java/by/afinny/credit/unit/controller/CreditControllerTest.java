package by.afinny.credit.unit.controller;

import by.afinny.credit.controller.CreditController;
import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditCardBalanceDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.dto.ResponseOperationDto;
import by.afinny.credit.service.CreditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditController.class)
class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CreditService creditService;

    private final UUID CLIENT_ID = UUID.randomUUID();
    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static CreditBalanceDto creditBalanceDto;
    private static CreditScheduleDto creditScheduleDto;
    private static DetailsDto detailsDto;
    private final static UUID CREDIT_ID = UUID.randomUUID();
    private final static UUID AGREEMENT_ID = UUID.randomUUID();

    private static List<ResponseOperationDto> responseOperations;

    @BeforeAll
    static void setUp() {
        CreditCardBalanceDto creditCardBalanceDto = CreditCardBalanceDto.builder()
                .cardId(UUID.randomUUID())
                .balance(new BigDecimal(8520))
                .cardNumber("885515")
                .build();

        creditBalanceDto = CreditBalanceDto.builder()
                .creditLimit(new BigDecimal(1000))
                .interestDebt(new BigDecimal(10))
                .principalDebt(new BigDecimal(100))
                .interestRate(new BigDecimal(50))
                .paymentPrincipal(new BigDecimal(20))
                .paymentInterest(new BigDecimal(10))
                .paymentDate(LocalDate.now())
                .accountCurrencyCode("RUB")
                .creditCurrencyCode("RUB")
                .accountNumber("5584")
                .name("XVZ")
                .agreementNumber("558844")
                .agreementId(UUID.randomUUID())
                .card(creditCardBalanceDto)
                .build();

        creditScheduleDto = CreditScheduleDto.builder()
                .accountNumber("1")
                .agreementID(UUID.randomUUID())
                .interestDebt(BigDecimal.valueOf(1337))
                .paymentsSchedule(new ArrayList<>())
                .principalDebt(BigDecimal.valueOf(13372)).build();

        detailsDto = DetailsDto.builder()
                .agreementId(UUID.randomUUID())
                .creditCurrencyCode("RUB")
                .principalDebt(new BigDecimal(123))
                .interestDebt(new BigDecimal(321))
                .principal(new BigDecimal(123))
                .interest(new BigDecimal(523)).build();

        responseOperations = List.of(ResponseOperationDto.builder()
                        .operationId(UUID.randomUUID())
                        .completedAt(LocalDateTime.now())
                        .details("test")
                        .accountId(UUID.randomUUID())
                        .operationType("test")
                        .currencyCode("123")
                        .type("test")
                .build());
    }

    @Test
    @DisplayName("if the list of operation was successfully received then return status OK")
    void getDetailsOfLastPayments_shouldReturnResponseOperations() throws Exception {
        //ARRANGE
        when(creditService.getDetailsOfLastPayments(CLIENT_ID, CREDIT_ID, 0, 4)).thenReturn(responseOperations);

        //ACT
        MvcResult mvcResult = mockMvc.perform(get(
                        CreditController.URL_CREDITS +
                                CreditController.URL_CREDIT_ID +
                                CreditController.URL_CREDIT_HISTORY, CREDIT_ID)
                        .param("clientId", CLIENT_ID.toString())
                        .param("pageNumber", String.valueOf(0))
                        .param("pageSize", String.valueOf(4))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(responseOperations)))
                .andExpect(status().isOk())
                .andReturn();
        //VERIFY
        verifyBody(asJsonString(responseOperations), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("if the list of operation wasn't successfully received then return Internal Server Error")
    void getDetailsOfLastPayments_ifNotSuccess_thenStatus500() throws Exception {
        //ARRANGE
        when(creditService.getDetailsOfLastPayments(CLIENT_ID, CREDIT_ID, 0, 4)).thenThrow(new RuntimeException());

        //ACT & VERIFY
        mockMvc.perform(get(
                        CreditController.URL_CREDITS +
                                CreditController.URL_CREDIT_ID +
                                CreditController.URL_CREDIT_HISTORY, CREDIT_ID)
                        .param("clientId", CLIENT_ID.toString())
                        .param("pageNumber", String.valueOf(0))
                        .param("pageSize", String.valueOf(4))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(responseOperations)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If the list of current credits was successfully received then return status OK")
    void getClientCurrentCredits_ifSuccess_thenStatus200() throws Exception {
        //ARRANGE
        when(creditService.getClientCreditsWithActiveStatus(CLIENT_ID)).thenReturn(new ArrayList<>());

        //ACT
        ResultActions perform = mockMvc.perform(
                get(CreditController.URL_CREDITS)
                        .param(CreditController.PARAM_CLIENT_ID, CLIENT_ID.toString()));

        //VERIFY
        perform.andExpect(status().isOk());
        MvcResult result = perform.andReturn();
        verifyClientIdRequestParameter(result);
        assertThatResponseBodyIsEmptyArray(result);
    }

    @Test
    @DisplayName("If the list of current credits wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getClientCurrentCredits_ifNotSuccess_thenStatus500() throws Exception {
        //ARRANGE
        when(creditService.getClientCreditsWithActiveStatus(CLIENT_ID)).thenThrow(new RuntimeException());

        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(CreditController.URL_CREDITS)
                        .param(CreditController.PARAM_CLIENT_ID, CLIENT_ID.toString()));

        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If credit was successfully received then return status OK")
    void getCredit_ifSuccess_then200_OK() throws Exception {
        //ARRANGE
        when(creditService.getCreditBalance(any(UUID.class), any(UUID.class))).thenReturn(creditBalanceDto);

        //ACT &
        ResultActions resultActions = mockMvc.perform(
                get(CreditController.URL_CREDITS + CreditController.URL_CREDIT_ID, CREDIT_ID)
                        .param("clientId", CLIENT_ID.toString()));

        //VERIFY
        resultActions.andExpect(status().isOk());

        String creditIdParam = Arrays.stream(resultActions.andReturn().getRequest().getRequestURI().split("/"))
                .skip(3).findFirst().orElse(null);
        assertThat(creditIdParam)
                .isNotNull()
                .isEqualTo(CREDIT_ID.toString());

        String actualResponseBody = resultActions.andReturn().getResponse().getContentAsString();
        String expectedResponseBody = asJsonString(creditBalanceDto);
        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("If credit wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getCredit_ifNotSuccess_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(creditService.getCreditBalance(any(UUID.class), any(UUID.class))).thenThrow(RuntimeException.class);

        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(CreditController.URL_CREDITS + CreditController.URL_CREDIT_ID, "1")
                        .param("clientId", CLIENT_ID.toString()));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If credit payments schedule was successfully received then return status OK")
    void getCreditPaymentSchedule_ifSuccess_then200_OK() throws Exception {
        //ARRANGE
        when(creditService.getPaymentSchedule(CLIENT_ID, CREDIT_ID)).thenReturn(creditScheduleDto);

        //ACT
        String actualResponseBody = mockMvc.perform(get(
                CreditController.URL_CREDITS +
                        CreditController.URL_CREDIT_ID +
                        CreditController.URL_CREDIT_SCHEDULE, CREDIT_ID)
                        .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        //VERIFY
        String expectedResponseBody = asJsonString(creditScheduleDto);
        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("If credit payments schedule wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getCreditPaymentSchedule_ifNotSuccess_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(creditService.getPaymentSchedule(CLIENT_ID, CREDIT_ID)).thenThrow(RuntimeException.class);

        //ACT
        ResultActions resultActions = mockMvc.perform(get(
                CreditController.URL_CREDITS +
                        CreditController.URL_CREDIT_ID +
                        CreditController.URL_CREDIT_SCHEDULE, CREDIT_ID));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If details for credit payment was successfully received then return status OK")
    void getDetailsForPayment_ifSuccess_then200_OK() throws Exception {
        //ARRANGE
        when(creditService.getDetailsForPayment(CLIENT_ID, AGREEMENT_ID)).thenReturn(detailsDto);

        //ACT
        String actualResponseBody = mockMvc.perform(get(
                        CreditController.URL_CREDITS +
                                CreditController.URL_AGREEMENT_ID +
                                CreditController.URL_CREDIT_DETAILS, AGREEMENT_ID)
                        .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        //VERIFY
        String expectedResponseBody = asJsonString(detailsDto);
        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("If details for credit payment wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getDetailsForPayment_ifNotSuccess_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(creditService.getDetailsForPayment(CLIENT_ID, AGREEMENT_ID)).thenThrow(RuntimeException.class);

        //ACT
        ResultActions resultActions = mockMvc.perform(get(
                CreditController.URL_CREDITS +
                        CreditController.URL_AGREEMENT_ID +
                        CreditController.URL_CREDIT_DETAILS, AGREEMENT_ID));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }
    private static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(FORMAT_DATE))
                .registerModule(new JavaTimeModule())
                .writeValueAsString(obj);
    }

    private void verifyClientIdRequestParameter(MvcResult result) {
        assertThat(result.getRequest().getParameter(CreditController.PARAM_CLIENT_ID)).isEqualTo(CLIENT_ID.toString());
    }

    private void assertThatResponseBodyIsEmptyArray(MvcResult result) throws UnsupportedEncodingException {
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    private void verifyBody(String expectedBody, String actualBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}