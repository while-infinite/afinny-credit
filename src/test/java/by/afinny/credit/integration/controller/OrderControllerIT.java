package by.afinny.credit.integration.controller;

import by.afinny.credit.controller.OrderController;
import by.afinny.credit.dto.RequestCreditOrderDto;
import by.afinny.credit.dto.ResponseCreditOrderDto;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.integration.config.annotation.TestWithPostgresContainer;
import by.afinny.credit.mapper.CreditOrderMapper;
import by.afinny.credit.repository.CreditOrderRepository;
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
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithPostgresContainer
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration test for order ")
public class OrderControllerIT {
    public Product product;
    public CreditOrder creditOrder;
    private final UUID CLIENT_ID = UUID.fromString("9af9fe9a-23b7-4d55-b3dc-a929b86c87b4");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CreditOrderMapper creditOrderMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CreditOrderRepository creditOrderRepository;
    private ResponseCreditOrderDto responseCreditOrderDto;
    @Autowired
    private InitClass initClass;
    @Autowired
    private CompareClass compareClass;


    @BeforeEach
    void save() {

        product = initClass.setUpProduct();

        creditOrder = initClass.setUpCreditOrder();

        creditOrder.setProduct(product);
        creditOrderRepository.save(creditOrder);

        productRepository.save(product);


    }


    @Test
    @DisplayName("If credit order was successfully created then return status OK")
    void createOrder_ifSuccessfullyCreated_then200_OK() throws Exception {

        RequestCreditOrderDto requestCreditOrderDto = RequestCreditOrderDto.builder()
                .productId(productRepository.findAll().get(0).getId())
                .creationDate(LocalDate.now())
                .amount(new BigDecimal(1))
                .periodMonths(3)
                .monthlyExpenditure(new BigDecimal(1))
                .monthlyIncome(new BigDecimal(1))
                .employerIdentificationNumber("1111111111").build();

        //ACT
        ResultActions perform = mockMvc.perform(
                post(OrderController.URL_CREDIT_ORDERS + OrderController.URL_NEW)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param(OrderController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID))
                        .content(compareClass.asJsonString(requestCreditOrderDto)));

        //VERIFY
        perform.andExpect(status().isOk());
        MvcResult result = perform.andReturn();

        String actual = result.getResponse().getContentAsString();

        responseCreditOrderDto = ResponseCreditOrderDto.builder()
                .id(creditOrderRepository.findAll().get(1).getId())
                .productId(productRepository.findAll().get(0).getId())
                .productName(productRepository.findAll().get(0).getName())
                .status(creditOrderRepository.findAll().get(0).getStatus())
                .amount(requestCreditOrderDto.getAmount())
                .periodMonths(requestCreditOrderDto.getPeriodMonths())
                .creationDate(requestCreditOrderDto.getCreationDate())
                .build();

        String expected = compareClass.asJsonString(responseCreditOrderDto);

        compareClass.verifyClientIdRequestParameter(result);
        compareClass.verifyBody(actual, expected);

    }

    @Test
    @DisplayName("If credit order was successfully found then return status OK")
    void getClientId_ifSuccessfullyFoundCreditOrder_then200_OK() throws Exception {

        responseCreditOrderDto = creditOrderMapper.creditOrderToResponseDto(
                creditOrderRepository.findAll().get(0)
        );

        List<ResponseCreditOrderDto> responseList = List.of(responseCreditOrderDto);
        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(OrderController.URL_CREDIT_ORDERS)
                        .param(OrderController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID)));

        //VERIFY
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();

        compareClass.verifyClientIdRequestParameter(result);
        compareClass.verifyBody(compareClass.asJsonString(responseList), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If credit order was successfully delete then return status No_Content")
    void deleteCreditOrder_ifSuccessfullyDeleted_then204_NO_CONTENT() throws Exception {


        //ACT
        ResultActions perform = mockMvc.perform(
                delete(OrderController.URL_CREDIT_ORDERS
                        + OrderController.PARAM_CREDIT_ORDER_ID, creditOrderRepository.findAll().get(0).getId())
                        .param(OrderController.PARAM_CLIENT_ID, String.valueOf(CLIENT_ID)));

        //VERIFY
        perform.andExpect(status().isNoContent());
        String afterDelete = creditOrderRepository.findAll().toString();

        assertThat(afterDelete).isEqualTo("[]");

    }


}
