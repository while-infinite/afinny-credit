package by.afinny.credit.kafka;

import by.afinny.credit.dto.CreditCardPinCodeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty("kafka.topics.new-pin-code-card-producer.enabled")
public class CardNewPinCodeSource {

    private final KafkaTemplate<String, ?> kafkaTemplate;

    @Value("${kafka.topics.new-pin-code-card-producer.path}")
    private String kafkaTopic;

    @EventListener
    public void sendMessageAboutCardPinCodeChange(CreditCardPinCodeDto creditCardPinCodeDto) {
        log.info("Dto " + creditCardPinCodeDto + " has been received, sending message...");
        kafkaTemplate.send(
                MessageBuilder
                        .withPayload(creditCardPinCodeDto)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                        .setHeader(KafkaHeaders.TOPIC, kafkaTopic)
                        .build()
        );
    }
}
