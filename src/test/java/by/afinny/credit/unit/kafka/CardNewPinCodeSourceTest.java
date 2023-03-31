package by.afinny.credit.unit.kafka;

import by.afinny.credit.dto.CreditCardPinCodeDto;
import by.afinny.credit.kafka.CardNewPinCodeSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class CardNewPinCodeSourceTest {
    @Autowired
    private CardNewPinCodeSource source;

    @MockBean
    private KafkaTemplate<String, ?> kafkaTemplate;

    @Value("${kafka.topics.new-pin-code-card-producer.path}")
    private String KAFKA_TOPIC;
    private CreditCardPinCodeDto creditCardPinCodeDto;

    @BeforeAll
    void setUp(){
        creditCardPinCodeDto = CreditCardPinCodeDto.builder()
                .cardNumber("123456")
                .newPin("0000")
                .build();
    }


    @Test
    @DisplayName("verify sending message to kafka broker")
    void sendMessageAboutCardPinCodeChange() {
        //ARRANGE
        ArgumentCaptor<Message<?>> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

        //ACT
        source.sendMessageAboutCardPinCodeChange(creditCardPinCodeDto);

        //VERIFY
        verify(kafkaTemplate).send(messageArgumentCaptor.capture());
        Message<?> message = messageArgumentCaptor.getValue();

        assertThat(message.getPayload()).isEqualTo(creditCardPinCodeDto);
        assertThat(message.getHeaders()).containsEntry(KafkaHeaders.TOPIC, KAFKA_TOPIC);
    }
}