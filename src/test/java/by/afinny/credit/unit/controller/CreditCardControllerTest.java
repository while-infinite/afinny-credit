package by.afinny.credit.unit.controller;

import by.afinny.credit.controller.CreditCardController;
import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardDto;
import by.afinny.credit.dto.CreditCardLimitDto;
import by.afinny.credit.dto.CreditCardPinCodeDto;
import by.afinny.credit.dto.RequestCardStatusDto;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.entity.constant.PaymentSystem;
import by.afinny.credit.exception.CardStatusesAreEqualsException;
import by.afinny.credit.exception.handler.ExceptionHandlerController;
import by.afinny.credit.service.CreditCardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(CreditCardController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreditCardControllerTest {

    @MockBean
    private CreditCardService creditCardService;

    private final UUID CARD_ID = UUID.fromString("6841e664-090b-4d30-8e45-2424aee6f266");
    private final UUID CLIENT_ID = UUID.randomUUID();
    private final String CARD_NUMBER = "1234567890";
    private CreditCardDto creditCardDto;
    private CreditCardLimitDto creditCardLimitDto;
    private List<CreditCardDto> creditCardDtoList;
    private CardInfoDto cardInfoDto;
    private MockMvc mockMvc;
    private RequestCardStatusDto requestCardStatus;
    private CreditCardPinCodeDto creditCardPinCodeDto;

    @BeforeAll
    void setUp() {
        mockMvc = standaloneSetup(new CreditCardController(creditCardService))
                .setControllerAdvice(new ExceptionHandlerController()).build();

        creditCardDto = CreditCardDto.builder()
                .id(UUID.randomUUID())
                .accountNumber("123")
                .cardNumber("11112223333444")
                .balance(BigDecimal.valueOf(10000.00))
                .currencyCode("181")
                .build();

        creditCardDtoList = List.of(creditCardDto);

        requestCardStatus = RequestCardStatusDto.builder()
                .cardNumber("1234567890")
                .cardStatus(CardStatus.ACTIVE)
                .build();

        cardInfoDto = CardInfoDto.builder()
                .creditId(UUID.randomUUID())
                .accountNumber("123")
                .balance(BigDecimal.valueOf(5000.00))
                .holderName("holder_name")
                .expirationDate(LocalDate.of(2024, 1, 2).toString())
                .paymentSystem(PaymentSystem.AMERICAN_EXPRESS)
                .status(CardStatus.BLOCKED)
                .transactionLimit(new BigDecimal(700))
                .name("TEST")
                .principalDebt(BigDecimal.valueOf(1))
                .creditLimit(BigDecimal.valueOf(150000))
                .creditCurrencyCode("182")
                .terminationDate(LocalDate.of(2024, 1, 2).toString())
                .build();

        creditCardLimitDto = CreditCardLimitDto.builder()
                .cardNumber("1234567890")
                .transactionLimit(new BigDecimal(1000))
                .build();

        creditCardPinCodeDto = CreditCardPinCodeDto.builder()
                .cardNumber("123456")
                .newPin("0000")
                .build();
    }

    @Test
    @DisplayName("If successfully changed status then return OK")
    void changeCardStatus_shouldReturnCardStatus() throws Exception {
        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.URL_CARDS + CreditCardController.URL_CARDS_ACTIVE)
                                .param("clientId", CLIENT_ID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(requestCardStatus)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("If changing status wasn't successfully received then return Internal Server Error")
    void changeCardStatus_ifNotSuccess_thenStatus500() throws Exception {
        //ARRANGE
        doThrow(new RuntimeException()).when(creditCardService)
                .changeCardStatus(any(UUID.class), any(RequestCardStatusDto.class));

        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.URL_CARDS + CreditCardController.URL_CARDS_ACTIVE)
                                .param("clientId", CLIENT_ID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(requestCardStatus)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If changing status wasn't successfully received then return Bad Request")
    void changeCardStatus_ifNotSuccess_thenStatus400() throws Exception {
        //ARRANGE
        doThrow(new CardStatusesAreEqualsException(
                Integer.toString(HttpStatus.BAD_REQUEST.value()),
                "The same card status already exists!")).when(creditCardService)
                .changeCardStatus(any(UUID.class), any(RequestCardStatusDto.class));

        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.URL_CARDS + CreditCardController.URL_CARDS_ACTIVE)
                                .param("clientId", CLIENT_ID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(requestCardStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("If credit cards was successfully found then return list of credit cards")
    void getCreditCards_shouldReturnListCardsDto() throws Exception {
        //ARRANGE
        when(creditCardService.getCreditCards(CLIENT_ID)).thenReturn(creditCardDtoList);

        //ACT & VERIFY
        MvcResult result = mockMvc.perform(get(CreditCardController.URL_CARDS)
                        .param(CreditCardController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn();

        verifyClientIdParameter(result.getRequest().getParameter(CreditCardController.PARAM_CLIENT_ID));
        verifyBody(result.getResponse().getContentAsString(), asJsonString(creditCardDtoList));
    }

    @Test
    @DisplayName("If the list of credit cards wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getCreditCards_ifNotSuccess_thenStatus500() throws Exception {
        //ARRANGE
        when(creditCardService.getCreditCards(CLIENT_ID)).thenThrow(new RuntimeException());

        //ACT & VERIFY
        MvcResult result = mockMvc.perform(get(CreditCardController.URL_CARDS)
                        .param(CreditCardController.PARAM_CLIENT_ID, CLIENT_ID.toString()))
                .andExpect(status().isInternalServerError())
                .andReturn();

        verifyClientIdParameter(result.getRequest().getParameter(CreditCardController.PARAM_CLIENT_ID));
    }

    @Test
    @DisplayName("if card was successfully found then return status OK")
    void getCardInformation_shouldReturnCardBalance() throws Exception {
        //ARRANGE
        when(creditCardService.getCardInformation(any(UUID.class), any(UUID.class))).thenReturn(cardInfoDto);

        //ACT
        MvcResult result = mockMvc.perform(
                        get(CreditCardController.URL_CARDS_CLIENT_ID)
                                .param(CreditCardController.PARAM_CARD_ID, CARD_ID.toString())
                                .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        verifyBody(asJsonString(cardInfoDto), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("if card wasn't successfully found then return status INTERNAL_SERVER_ERROR")
    void getCardInformation_ifNotFoundCard_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(creditCardService.getCardInformation(any(UUID.class), any(UUID.class))).thenThrow(RuntimeException.class);

        //ACT & VERIFY
        mockMvc.perform(
                        get("/auth/credit-cards/info", CARD_ID.toString())
                                .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("If successfully set credit card limit then return OK")
    void setCreditCardLimit_shouldNotReturnContent() throws Exception {
        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.SET_CREDIT_CARD_LIMIT_URL)
                                .param("clientId", CLIENT_ID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(creditCardLimitDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("If set credit card limit wasn't successfully received then return INTERNAL SERVER ERROR")
    void setCreditCardLimit_ifSetFailed_thenThrow() throws Exception {
        //ARRANGE
        doThrow(new RuntimeException()).when(creditCardService)
                .setCreditCardLimit(any(UUID.class), any(CreditCardLimitDto.class));

        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.SET_CREDIT_CARD_LIMIT_URL)
                                .param("clientId", CLIENT_ID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(creditCardLimitDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("if card was successfully found then return status OK")
    void getCardNumber_shouldReturnCardNumber() throws Exception {
        //ARRANGE
        when(creditCardService.getCardNumber(any(UUID.class), any(UUID.class))).thenReturn(CARD_NUMBER);

        //ACT
        MvcResult result = mockMvc.perform(
                        get(CreditCardController.URL_CARDS + CreditCardController.URL_INFORMATION, CARD_ID.toString())
                                .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        verifyBody(result.getResponse().getContentAsString(), CARD_NUMBER);
    }

    @Test
    @DisplayName("if card wasn't successfully found then return status INTERNAL_SERVER_ERROR")
    void getCardNumber_ifNotFoundCard_then500_INTERNAL_SERVER_ERROR() throws Exception {
        //ARRANGE
        when(creditCardService.getCardNumber(any(UUID.class), any(UUID.class)))
                .thenThrow(RuntimeException.class);

        //ACT & VERIFY
        mockMvc.perform(
                        get(CreditCardController.URL_CARDS + CreditCardController.URL_INFORMATION, CARD_ID.toString())
                                .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("if card deleted return ok")
    void deleteCreditCard() throws Exception {
        mockMvc.perform(
                        delete(CreditCardController.URL_CARDS)
                                .param(CreditCardController.PARAM_CARD_ID, String.valueOf(CARD_ID))
                                .param("clientId", CLIENT_ID.toString()))
                .andExpect(status().is(204));
    }

    @Test
    @DisplayName("If successfully change pin-code credit card then return status OK")
    void changeCardPinCode_shouldNotReturnContent() throws Exception {
        //ACT
        MvcResult result = mockMvc.perform(post(
                        CreditCardController.URL_CARDS + CreditCardController.URL_CARD_PIN_CODE)
                        .param("clientId", CLIENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(creditCardPinCodeDto)))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        verifyBody(asJsonString(creditCardPinCodeDto), new String(Objects.requireNonNull(result.getRequest().getContentAsByteArray())));
    }

    @Test
    @DisplayName("If change pin-code credit card failed then return InternalServerError")
    void changeCardPinCode_ifNotSent_then500_InternalServerError() throws Exception {
        //ARRANGE
        doThrow(RuntimeException.class).when(creditCardService).changeCardPinCode(any(UUID.class), any(CreditCardPinCodeDto.class));

        //ACT & VERIFY
        ResultActions perform = mockMvc.perform(post(
                CreditCardController.URL_CARDS + CreditCardController.URL_CARD_PIN_CODE)
                .param("clientId", CLIENT_ID.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(creditCardPinCodeDto)));
        perform.andExpect(status().isInternalServerError());
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        return new ObjectMapper().findAndRegisterModules().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(obj);
    }

    private void verifyBody(String actualBody, String expectedBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    private void verifyClientIdParameter(String clientId) {
        UUID clientIdParameter = UUID.fromString(clientId);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(clientIdParameter)
                    .withFailMessage("Client id parameter should be set")
                    .isNotNull();
            softAssertions.assertThat(clientIdParameter)
                    .withFailMessage("Client id parameter should be " + CLIENT_ID + " instead of " + clientIdParameter)
                    .isEqualTo(CLIENT_ID);
        });
    }
}