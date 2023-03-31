package by.afinny.credit.unit.kafka;

import by.afinny.credit.dto.kafka.EmployerEvent;
import by.afinny.credit.kafka.CreditSource;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class CreditSourceTest {

    @Autowired
    private CreditSource source;

    @MockBean
    private KafkaTemplate<String, ?> kafkaTemplate;

    private static EmployerEvent event;
    private final String KAFKA_TOPIC = "test";

    @BeforeAll
    static void setUp() {
        event = new EmployerEvent();
        event.setClientId(UUID.randomUUID());
        event.setEmployerIdentificationNumber(RandomString.make());
    }

    @Test
    @DisplayName("Verify sending message to kafka broker")
    void sendMessageAboutEmployerUpdate() {
        //ARRANGE
        ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        //ACT
        source.sendMessageAboutEmployerUpdate(event);

        //VERIFY
        verify(kafkaTemplate).send(messageCaptor.capture());
        Message<?> message = messageCaptor.getValue();

        assertThat(message.getPayload()).isEqualTo(event);
        assertThat(message.getHeaders()).containsEntry(KafkaHeaders.TOPIC, KAFKA_TOPIC);
    }
}