package by.afinny.credit.kafka;

import by.afinny.credit.dto.kafka.CardEvent;
import by.afinny.credit.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardStatusTopicListener {

    private final CreditCardService cardService;

    @KafkaListener(
            topics = "${kafka.topics.bank-system-listener.path}",
            groupId = "card-service",
            containerFactory = "kafkaListener")
    public void onRequestUpdateCardStatusEvent(Message<CardEvent> message) {
        CardEvent event = message.getPayload();
        log.info("Processing event: card number = " + event.getCardNumber() + ", card status = " + event.getCardStatus());
        cardService.modifyCardStatus(event.getClientId(), event.getCardNumber(), event.getCardStatus());
    }
}
