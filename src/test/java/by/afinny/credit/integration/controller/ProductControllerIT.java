package by.afinny.credit.integration.controller;

import by.afinny.credit.controller.ProductController;
import by.afinny.credit.dto.ProductDto;
import by.afinny.credit.entity.CreditOrder;
import by.afinny.credit.entity.Product;
import by.afinny.credit.integration.config.annotation.TestWithPostgresContainer;
import by.afinny.credit.mapper.ProductMapperImpl;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithPostgresContainer
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integration test for product ")
public class ProductControllerIT {
    public Product product;
    public CreditOrder creditOrder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductMapperImpl productMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CreditOrderRepository creditOrderRepository;

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
    @DisplayName("If the list of products was successfully received then return status OK")
    void getProducts_ifSuccess_thenStatus200() throws Exception {
        //ACT
        ResultActions perform = mockMvc.perform(
                get(ProductController.URL_CREDIT_PRODUCTS));

        //VERIFY
        perform.andExpect(status().isOk());
        MvcResult result = perform.andReturn();

        String actual = result.getResponse().getContentAsString();

        List<Product> products = new ArrayList<>();
        products.add(productRepository.findAll().get(0));
        List<ProductDto> productDtoList = productMapper.productsToProductsDto(products);

        String expected = compareClass.asJsonString(productDtoList);

        compareClass.verifyBody(actual, expected);

    }


}