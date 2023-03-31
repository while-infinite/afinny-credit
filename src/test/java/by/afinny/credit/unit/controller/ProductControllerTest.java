package by.afinny.credit.unit.controller;

import by.afinny.credit.controller.ProductController;
import by.afinny.credit.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("If the list of products was successfully received then return status OK")
    void getProducts_ifSuccess_thenStatus200() throws Exception {
        //ARRANGE
        when(productService.getProducts()).thenReturn(new ArrayList<>());

        //ACT
        ResultActions perform = mockMvc.perform(
                get(ProductController.URL_CREDIT_PRODUCTS));

        //VERIFY
        perform.andExpect(status().isOk());
        MvcResult result = perform.andReturn();
        assertThatResponseBodyIsEmptyArray(result);
    }

    @Test
    @DisplayName("If the list of products wasn't successfully received then return status INTERNAL SERVER ERROR")
    void getProducts_ifNotSuccess_thenStatus500() throws Exception {
        //ARRANGE
        when(productService.getProducts()).thenThrow(new RuntimeException());

        //ACT
        ResultActions resultActions = mockMvc.perform(
                get(ProductController.URL_CREDIT_PRODUCTS));

        //VERIFY
        resultActions.andExpect(status().isInternalServerError());
    }

    private void assertThatResponseBodyIsEmptyArray(MvcResult result) throws UnsupportedEncodingException {
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }
}

