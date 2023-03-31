package by.afinny.credit.unit.repository;

import by.afinny.credit.entity.Card;
import by.afinny.credit.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema-h2.sql", "/data-h2.sql"}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class CardRepositoryTest {

    @MockBean
    private CardRepository cardRepository;
    private final String CARD_NUMBER = "1111222233334444";
    private final UUID CLIENT_ID = UUID.randomUUID();

    private Card card;

    @BeforeEach
    private void setUp() {
        card = Card.builder()
                .cardNumber(CARD_NUMBER)
                .build();
    }

    @Test
    @DisplayName("Check card by card number")
    void findCardByCardNumber() {
        when(cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER)).thenReturn(Optional.of(card));

        Card cardByCardNumber = cardRepository.findByAccountCreditCreditOrderClientIdAndCardNumber(CLIENT_ID, CARD_NUMBER).get();

        assertThat(cardByCardNumber.getCardNumber()).isEqualTo(CARD_NUMBER);
    }
}