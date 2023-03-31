package by.afinny.credit.kafka;

import by.afinny.credit.dto.kafka.EmployerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.MimeTypeUtils;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty("kafka.topics.card-status-listener.enabled")
public class CreditSource {

    private final KafkaTemplate<String, ?> kafkaTemplate;

    @Value("${kafka.topics.user-service-listener.path}")
    private String kafkaTopic;

    @TransactionalEventListener
    public void sendMessageAboutEmployerUpdate(EmployerEvent event) {
        log.info("Event " + event + " has been received, sending message...");
        kafkaTemplate.send(
                MessageBuilder
                        .withPayload(event)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .setHeader(KafkaHeaders.TOPIC, kafkaTopic)
                        .build()
        );
    }
}
