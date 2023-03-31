package by.afinny.credit.utils;

import by.afinny.credit.controller.CreditController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class CompareClass {

    private final UUID CLIENT_ID = UUID.fromString("9af9fe9a-23b7-4d55-b3dc-a929b86c87b4");
    private static final String FORMAT_DATE = "yyyy-MM-dd";

    public String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(FORMAT_DATE))
                .registerModule(new JavaTimeModule())
                .writeValueAsString(obj);
    }

    public void verifyBody(String actualBody, String expectedBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    public void verifyClientIdRequestParameter(MvcResult result) {
        assertThat(result.getRequest().getParameter(CreditController.PARAM_CLIENT_ID))
                .isEqualTo(String.valueOf(CLIENT_ID));
    }
}
