package by.afinny.credit.integration.controller;

import by.afinny.credit.controller.CreditCardController;
import by.afinny.credit.dto.CardInfoDto;
import by.afinny.credit.dto.CreditCardDto;
import by.afinny.credit.dto.CreditCardLimitDto;
import by.afinny.credit.dto.CreditCardPinCodeDto;
import by.afinny.credit.dto.RequestCardStatusDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.integration.config.annotation.TestWithPostgresContainer;
import by.afinny.credit.mapper.CardMapperImpl;
import by.afinny.credit.repository.AccountRepository;
import by.afinny.credit.repository.AgreementRepository;
import by.afinny.credit.repository.CardRepository;
import by.afinny.credit.repository.CreditOrderRepository;
import by.afinny.credit.repository.CreditRepository;
import by.afinny.credit.repository.PaymentScheduleRepository;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.utils.CompareClass;
import by.afinny.credit.utils.InitClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithPostgresContainer
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration test for card ")
public class CreditCardControllerIT {
    public Product product;
    public CreditOrder creditOrder;
    public Credit credit;
    public Agreement agreement;
    public Account account;
    public PaymentSchedule paymentSchedule;
    public Card card;
    private final String CARD_NUMBER = "5855155555555555";
    private final UUID CLIENT_ID = UUID.fromString("9af9fe9a-23b7-4d55-b3dc-a929b86c87b4");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CardMapperImpl cardMapper;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private CreditOrderRepository creditOrderRepository;
    @Autowired
    private AgreementRepository agreementRepository;
    @Autowired
    private PaymentScheduleRepository paymentScheduleRepository;
    private RequestCardStatusDto requestCardStatus;
    @Autowired
    private InitClass initClass;
    @Autowired
    private CompareClass compareClass;

    @BeforeEach
    void save() {


        product = initClass.setUpProduct();

        creditOrder = initClass.setUpCreditOrder();

        credit = initClass.setUpCredit();

        agreement = initClass.setUpAgreement();

        account = initClass.setUpAccount();

        paymentSchedule = initClass.setUpPaymentSchedule();



        card = initClass.setUpCard();



        creditOrder.setProduct(product);
        creditOrderRepository.save(creditOrder);

        productRepository.save(product);


        credit.setCreditOrder(creditOrder);
        creditRepository.save(credit);

        agreement.setCredit(credit);
        agreementRepository.save(agreement);

        account.setCredit(credit);
        accountRepository.save(account);

        paymentSchedule.setAccount(account);
        paymentScheduleRepository.save(paymentSchedule);

        card.setAccount(account);
        cardRepository.save(card);

    }


    @Test
    @DisplayName("If successfully changed status then return OK")
    void changeCardStatus_shouldReturnCardStatus() throws Exception {

        requestCardStatus = RequestCardStatusDto.builder()
                .cardNumber(CARD_NUMBER)
                .cardStatus(CardStatus.CLOSED)
                .build();

        String cardStatusBefore = cardRepository.findAll().get(0).getStatus().toString();
        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.URL_CARDS + CreditCardController.URL_CARDS_ACTIVE)
                                .param("clientId", String.valueOf(CLIENT_ID))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(compareClass.asJsonString(requestCardStatus)))
                .andExpect(status().isOk());


        String cardStatusAfter = cardRepository.findAll().get(0).getStatus().toString();

        verifyChange(cardStatusBefore, cardStatusAfter);

    }

    @Test
    @DisplayName("If changing status wasn't successfully received then return Bad Request")
    void changeCardStatus_ifNotSuccess_thenStatus400() throws Exception {

        requestCardStatus = RequestCardStatusDto.builder()
                .cardNumber(CARD_NUMBER + 1)
                .cardStatus(CardStatus.CLOSED)
                .build();

        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.URL_CARDS + CreditCardController.URL_CARDS_ACTIVE)
                                .param("clientId", String.valueOf(CLIENT_ID))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(compareClass.asJsonString(requestCardStatus)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("If credit cards was successfully found then return list of credit cards")
    void getCreditCards_shouldReturnListCardsDto() throws Exception {

        //ACT & VERIFY
        MvcResult result = mockMvc.perform(get(CreditCardController.URL_CARDS)
                        .param(CreditCardController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn();

        verifyClientIdParameter(result.getRequest().getParameter(CreditCardController.PARAM_CLIENT_ID));

        List<CreditCardDto> creditCardDtoList = cardMapper.cardsToCardsDto(List.of(cardRepository.findAll().get(0)));

        compareClass.verifyBody(result.getResponse().getContentAsString(), compareClass.asJsonString(creditCardDtoList));
    }

    @Test
    @DisplayName("if card was successfully found then return status OK")
    void getCardInformation_shouldReturnCardBalance() throws Exception {

        //ACT
        MvcResult result = mockMvc.perform(
                        get(CreditCardController.URL_CARDS_CLIENT_ID)
                                .param(CreditCardController.PARAM_CARD_ID, cardRepository.findAll().get(0).getId().toString())
                                .param("clientId", String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn();

        CardInfoDto cardInfoDto = cardMapper.toCardInfoDto(
                cardRepository.findAll().get(0),
                accountRepository.findAll().get(0),
                creditRepository.findAll().get(0),
                productRepository.findAll().get(0),
                agreementRepository.findAll().get(0)

        );

        //VERIFY
        compareClass.verifyBody(compareClass.asJsonString(cardInfoDto), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If successfully set credit card limit then return OK")
    void setCreditCardLimit_shouldNotReturnContent() throws Exception {

        String creditCardLimitBefore = cardRepository.findAll().get(0).getTransactionLimit().toString();

        CreditCardLimitDto creditCardLimitDto = CreditCardLimitDto.builder()
                .cardNumber(CARD_NUMBER)
                .transactionLimit(new BigDecimal("20.00"))
                .build();


        //ACT & VERIFY
        mockMvc.perform(
                        patch(CreditCardController.SET_CREDIT_CARD_LIMIT_URL)
                                .param("clientId", String.valueOf(CLIENT_ID))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(compareClass.asJsonString(creditCardLimitDto)))
                .andExpect(status().isOk());

        String creditCardLimitAfter = cardRepository.findAll().get(0).getTransactionLimit().toString();

        verifyChange(creditCardLimitBefore, creditCardLimitAfter);

    }

    @Test
    @DisplayName("if card was successfully found then return status OK")
    void getCardNumber_shouldReturnCardNumber() throws Exception {

        //ACT
        MvcResult result = mockMvc.perform(
                        get(CreditCardController.URL_CARDS + CreditCardController.URL_INFORMATION, cardRepository.findAll().get(0).getId().toString())
                                .param("clientId", String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        compareClass.verifyBody(result.getResponse().getContentAsString(), cardRepository.findAll().get(0).getCardNumber());
    }

    @Test
    @DisplayName("if card deleted return ok")
    void deleteCreditCard() throws Exception {


        mockMvc.perform(
                        delete(CreditCardController.URL_CARDS)
                                .param(CreditCardController.PARAM_CARD_ID, String.valueOf(cardRepository.findAll().get(0).getId()))
                                .param("clientId", String.valueOf(CLIENT_ID)))
                .andExpect(status().is(204));


        String cardAfterDelete = accountRepository.findAll().get(0).getCard().toString();

        checkDelete(cardAfterDelete);

    }

    @Test
    @DisplayName("If successfully change pin-code credit card then return status OK")
    void changeCardPinCode_shouldNotReturnContent() throws Exception {

        CreditCardPinCodeDto creditCardPinCodeDto = CreditCardPinCodeDto.builder()
                .cardNumber(CARD_NUMBER)
                .newPin("9999")
                .build();

        //ACT
        MvcResult result = mockMvc.perform(post(
                        CreditCardController.URL_CARDS + CreditCardController.URL_CARD_PIN_CODE)
                        .param("clientId", String.valueOf(CLIENT_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(compareClass.asJsonString(creditCardPinCodeDto)))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        compareClass.verifyBody(compareClass.asJsonString(creditCardPinCodeDto), new String(Objects.requireNonNull(result.getRequest().getContentAsByteArray())));
    }


    private void verifyChange(String oldValue, String newValue) {
        assertThat(oldValue).isNotEqualTo(newValue);
    }

    private void checkDelete(String a) {
        assertThat(a).isEqualTo("[]");
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
