package by.afinny.credit.integration.controller;

import by.afinny.credit.controller.CreditController;
import by.afinny.credit.dto.CreditBalanceDto;
import by.afinny.credit.dto.CreditDto;
import by.afinny.credit.dto.CreditScheduleDto;
import by.afinny.credit.dto.DetailsDto;
import by.afinny.credit.entity.Account;
import by.afinny.credit.entity.Agreement;
import by.afinny.credit.entity.Card;
import by.afinny.credit.entity.Credit;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.PaymentSchedule;
import by.afinny.credit.entity.Product;
import by.afinny.credit.integration.config.annotation.TestWithPostgresContainer;
import by.afinny.credit.mapper.CreditMapper;
import by.afinny.credit.mapper.DetailsMapper;
import by.afinny.credit.repository.AccountRepository;
import by.afinny.credit.repository.AgreementRepository;
import by.afinny.credit.repository.CardRepository;
import by.afinny.credit.repository.CreditOrderRepository;
import by.afinny.credit.repository.CreditRepository;
import by.afinny.credit.repository.PaymentScheduleRepository;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.utils.CompareClass;
import by.afinny.credit.utils.InitClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.afinny.credit.entity.constant.CreditStatus.ACTIVE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithPostgresContainer
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration test for credit ")
public class CreditControllerIT {
    public Product product;
    public CreditOrder creditOrder;
    public Credit credit;
    public Agreement agreement;
    public Account account;
    public PaymentSchedule paymentSchedule;
    public Card card;
    private final UUID CLIENT_ID = UUID.fromString("9af9fe9a-23b7-4d55-b3dc-a929b86c87b4");
    @Autowired
    private InitClass initClass;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    CreditMapper creditMapper;
    @Autowired
    DetailsMapper detailsMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CreditOrderRepository creditOrderRepository;
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private AgreementRepository agreementRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PaymentScheduleRepository paymentScheduleRepository;
    @Autowired
    private CardRepository cardRepository;

    private List<PaymentSchedule> paymentScheduleList;

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

        paymentScheduleList = new ArrayList<>();


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

        paymentScheduleList.add(paymentScheduleRepository.findAll().get(0));


    }

    @Test
    @DisplayName("If the list of current credits was successfully received then return status OK")
    void getClientCurrentCredits_ifSuccess_thenStatus200() throws Exception {
        //ACT
        MvcResult result = mockMvc.perform(
                        get(CreditController.URL_CREDITS)
                                .param("clientId", String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn();

        //VERIFY
        List<CreditDto> creditDtoList = objectMapper.readValue(

                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CreditDto.class));

        String actualBody = creditDtoList.toString();

        String expectBody = creditMapper.creditsToCreditDto(creditRepository
                .findByCreditOrderClientIdAndStatus(CLIENT_ID, ACTIVE)).toString();

        compareClass.verifyClientIdRequestParameter(result);

        compareClass.verifyBody(actualBody, expectBody);
    }


    @Test
    @DisplayName("If credit was successfully received then return status OK")
    void getCredit_ifSuccess_then200_OK() throws Exception {

        //ACT &
        ResultActions resultActions = mockMvc.perform(
                get(CreditController.URL_CREDITS + CreditController.URL_CREDIT_ID, creditRepository.findAll().get(0).getId())
                        .param("clientId", String.valueOf(CLIENT_ID)));

        //VERIFY
        resultActions.andExpect(status().isOk());

        String actualResponseBody = resultActions.andReturn().getResponse().getContentAsString();

        CreditBalanceDto creditBalanceDto = creditMapper.toCreditBalanceDto(
                creditRepository.findAll().get(0).getCreditOrder().getProduct(),
                creditRepository.findAll().get(0),
                creditRepository.findAll().get(0).getAgreement(),
                accountRepository.findAll().get(0),
                card,
                paymentScheduleRepository.findAll().get(0)
        );


        String expectedResponseBody = compareClass.asJsonString(creditBalanceDto);

        compareClass.verifyBody(actualResponseBody, expectedResponseBody);

    }

    @Test
    @DisplayName("If credit wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getCredit_ifNotSuccess_then500_INTERNAL_SERVER_ERROR() throws Exception {

        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(CreditController.URL_CREDITS + CreditController.URL_CREDIT_ID, "1")
                        .param("clientId", UUID.randomUUID().toString()));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }


    @Test
    @DisplayName("If credit payments schedule was successfully received then return status OK")
    void getCreditPaymentSchedule_ifSuccess_then200_OK() throws Exception {

        //ACT
        String actualResponseBody = mockMvc.perform(get(
                        CreditController.URL_CREDITS +
                                CreditController.URL_CREDIT_ID +
                                CreditController.URL_CREDIT_SCHEDULE, creditRepository.findAll().get(0).getId())
                        .param("clientId", String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        CreditScheduleDto creditScheduleDto = creditMapper.toCreditScheduleDto(
                agreementRepository.findAll().get(0),
                accountRepository.findAll().get(0),
                paymentScheduleList
        );
        String expectedResponseBody = compareClass.asJsonString(creditScheduleDto);

        compareClass.verifyBody(actualResponseBody, expectedResponseBody);
    }

    @Test
    @DisplayName("If credit payments schedule wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getCreditPaymentSchedule_ifNotSuccess_then500_INTERNAL_SERVER_ERROR() throws Exception {

        //ACT
        ResultActions resultActions = mockMvc.perform(get(
                CreditController.URL_CREDITS +
                        CreditController.URL_CREDIT_ID +
                        CreditController.URL_CREDIT_SCHEDULE, UUID.randomUUID()));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }


    @Test
    @DisplayName("If details for credit payment was successfully received then return status OK")
    void getDetailsForPayment_ifSuccess_then200_OK() throws Exception {

        //ACT
        String actualResponseBody = mockMvc.perform(get(
                        CreditController.URL_CREDITS +
                                CreditController.URL_AGREEMENT_ID +
                                CreditController.URL_CREDIT_DETAILS, agreementRepository.findAll().get(0).getId())
                        .param("clientId", String.valueOf(CLIENT_ID)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();


        //VERIFY
        DetailsDto detailsDto = detailsMapper.toDetailsDto(agreementRepository.findAll().get(0),
                accountRepository.findAll().get(0),
                creditRepository.findAll().get(0),
                paymentScheduleRepository.findAll().get(0)
        );
        String expectedResponseBody = compareClass.asJsonString(detailsDto);

        compareClass.verifyBody(actualResponseBody, expectedResponseBody);
    }

    @Test
    @DisplayName("If details for credit payment wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getDetailsForPayment_ifNotSuccess_then500_INTERNAL_SERVER_ERROR() throws Exception {

        //ACT
        ResultActions resultActions = mockMvc.perform(get(
                CreditController.URL_CREDITS +
                        CreditController.URL_AGREEMENT_ID +
                        CreditController.URL_CREDIT_DETAILS, UUID.randomUUID()));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }

}