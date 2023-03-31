package by.afinny.credit.integration.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@TestConfiguration
@ActiveProfiles("integration")
public class TestConfig {

    private final String FORMAT_DATE = "yyyy-MM-dd";

    @Bean
    public ObjectMapper objectMapper(JsonDeserializer<LocalDate> localDateJsonDeserializer) {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(FORMAT_DATE))
                .registerModule(new SimpleModule().addDeserializer(LocalDate.class, localDateJsonDeserializer));
    }

    @Bean
    public JsonDeserializer<LocalDate> localDateDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {

                return LocalDate.parse(jsonParser.readValueAs(String.class));
            }
        };
    }
}