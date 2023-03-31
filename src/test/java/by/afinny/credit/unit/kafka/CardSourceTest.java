package by.afinny.credit.unit.kafka;

import by.afinny.credit.dto.kafka.CardEvent;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.kafka.CardSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
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
class CardSourceTest {

    @Autowired
    private CardSource source;

    @MockBean
    private KafkaTemplate<String, ?> kafkaTemplate;

    private CardEvent event;
    private final String KAFKA_TOPIC = "test";

    @BeforeAll
    void setUp() {
        event = CardEvent.builder()
                .cardNumber("1234567890")
                .cardStatus(CardStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Verify sending message to kafka broker")
    void sendMessageAboutEmployerUpdate() {
        //ARRANGE
        ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        //ACT
        source.sendMessageAboutCardStatusUpdate(event);

        //VERIFY
        verify(kafkaTemplate).send(messageCaptor.capture());
        Message<?> message = messageCaptor.getValue();

        assertThat(message.getPayload()).isEqualTo(event);
        assertThat(message.getHeaders()).containsEntry(KafkaHeaders.TOPIC, KAFKA_TOPIC);
    }
}