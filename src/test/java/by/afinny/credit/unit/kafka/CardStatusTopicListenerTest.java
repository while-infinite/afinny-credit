package by.afinny.credit.unit.kafka;

import by.afinny.credit.dto.kafka.CardEvent;
import by.afinny.credit.entity.constant.CardStatus;
import by.afinny.credit.kafka.CardStatusTopicListener;
import by.afinny.credit.service.CreditCardService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class CardStatusTopicListenerTest {

    @InjectMocks
    private CardStatusTopicListener cardStatusTopicListener;

    @Mock
    private CreditCardService cardService;

    private CardEvent event;

    @BeforeAll
    void setUp() {
        event = CardEvent.builder()
                .clientId(UUID.randomUUID())
                .cardStatus(CardStatus.ACTIVE)
                .cardNumber("1234567890")
                .build();
    }

    @Test
    @DisplayName("When get request verify passed values through the service equality")
    void onRequestUpdateEmployerIdEvent() {
        //ARRANGE
        ArgumentCaptor<UUID> clientIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> cardNumberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CardStatus> cardStatusCaptor = ArgumentCaptor.forClass(CardStatus.class);
        //ACT
        cardStatusTopicListener.onRequestUpdateCardStatusEvent(new GenericMessage<>(event));
        //VERIFY
        verify(cardService).modifyCardStatus(clientIdCaptor.capture(), cardNumberCaptor.capture(), cardStatusCaptor.capture());
        verifyEvent(clientIdCaptor.getValue(), cardNumberCaptor.getValue(), cardStatusCaptor.getValue());
    }

    private void verifyEvent(UUID clientIdCaptor, String cardNumberCaptor, CardStatus cardStatusCaptor) {
        assertSoftly((softAssertions) -> {
            softAssertions.assertThat(clientIdCaptor).isEqualTo(event.getClientId());
            softAssertions.assertThat(cardStatusCaptor).isEqualTo(event.getCardStatus());
            softAssertions.assertThat(cardNumberCaptor).isEqualTo(event.getCardNumber());
        });
    }
}